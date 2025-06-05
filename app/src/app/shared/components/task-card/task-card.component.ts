import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [MatCardModule, MatChipsModule, MatIconModule, NgIf],
  templateUrl: './task-card.component.html',
  styleUrls: ['./task-card.component.css'],
})
export class TaskCardComponent {
  @Input() task: any;
}
