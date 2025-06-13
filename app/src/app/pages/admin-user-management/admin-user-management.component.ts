import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule, MatTableDataSource } from '@angular/material/table'; 
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort'; 
import { Subject, debounceTime, distinctUntilChanged, Subscription } from 'rxjs';
import { AdminUserManagementService } from './admin-user-management.service';
import { UserResponseDto } from 'src/app/shared/models/user-response.dto';
import { MatDialog, MatDialogModule } from '@angular/material/dialog'; 
import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component'; 
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; 

@Component({
  selector: 'app-admin-user-management',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSnackBarModule,
  ],
  templateUrl: './admin-user-management.component.html',
  styleUrls: ['./admin-user-management.component.css'] 
})
export class AdminUserManagementComponent implements OnInit, OnDestroy {
  isLoading = true;
  usersDataSource = new MatTableDataSource<UserResponseDto>();
  displayedColumns: string[] = ['id', 'username', 'email', 'systemRole', 'isLocked', 'isActive', 'actions'];


  totalUsers = 0;
  currentPage = 0;
  pageSize = 10;
  sortActive = 'username';
  sortDirection: 'asc' | 'desc' = 'asc';
  searchTerm: string = '';


  private searchSubject = new Subject<string>();
  private subscriptions: Subscription[] = [];


  availableSystemRoles: string[] = ['ADMIN', 'REGULAR_USER', 'VIEWER', 'GUEST'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private adminUserManagementService: AdminUserManagementService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {

    this.subscriptions.push(
      this.searchSubject.pipe(
        debounceTime(300),
        distinctUntilChanged()
      ).subscribe(term => {
        this.searchTerm = term;
        this.currentPage = 0;
        this.loadUsers();
      })
    );

    this.loadUsers(); 
  }

  ngAfterViewInit(): void {
 
    this.usersDataSource.sort = this.sort;
  
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadUsers(): void {
    this.isLoading = true;
    const sort = `${this.sortActive},${this.sortDirection}`;

    this.subscriptions.push(
      this.adminUserManagementService.searchUsers(this.searchTerm, this.currentPage, this.pageSize, sort)
        .subscribe({
          next: (page) => {
            this.usersDataSource.data = page.content;
            this.totalUsers = page.totalElements;
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Failed to load users:', err);
            this.snackBar.open('Failed to load users. Please try again.', 'Close', { duration: 3000 });
            this.usersDataSource.data = [];
            this.totalUsers = 0;
            this.isLoading = false;
          }
        })
    );
  }

  onSearchTermChange(event: Event): void {
    this.searchSubject.next((event.target as HTMLInputElement).value);
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadUsers();
  }

  onSortChange(sortState: Sort): void {
    this.sortActive = sortState.active;
    this.sortDirection = sortState.direction as 'asc' | 'desc';
    this.currentPage = 0; 
    this.loadUsers();
  }

  updateUserRole(user: UserResponseDto): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: `Change System Role for ${user.username}`,
        message: 'Select new role:',
        type: 'select', 
        options: this.availableSystemRoles,
        selectedValue: user.systemRole 
      }
    });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(result => {
        if (result && result.confirmed && result.value) {
          const newRole = result.value;
          this.isLoading = true;
          this.adminUserManagementService.updateUserSystemRole(user.id, newRole)
            .subscribe({
              next: (updatedUser) => {
                this.snackBar.open(`Successfully updated role for ${updatedUser.username} to ${updatedUser.systemRole}`, 'Close', { duration: 3000 });
               
                const index = this.usersDataSource.data.findIndex(u => u.id === updatedUser.id);
                if (index !== -1) {
                  this.usersDataSource.data[index] = updatedUser;
                  this.usersDataSource._updateChangeSubscription(); 
                }
                this.isLoading = false;
              },
              error: (err) => {
                console.error('Failed to update user role:', err);
                this.snackBar.open(`Failed to update role: ${err.error?.message || 'Unknown error'}`, 'Close', { duration: 5000 });
                this.isLoading = false;
              }
            });
        }
      })
    );
  }
}