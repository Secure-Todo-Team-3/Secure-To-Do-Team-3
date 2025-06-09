
resource "aws_vpc" "todo_team_vpc" {
  cidr_block           = var.cidr_block
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.name_prefix}-vpc"
  }
}

resource "aws_internet_gateway" "todo_igw" {
  vpc_id = aws_vpc.todo_team_vpc.id

  tags = {
    Name = "${var.name_prefix}-igw"
  }
}

resource "aws_subnet" "todo_public_subnet" {
  count             = length(var.availability_zones)
  vpc_id            = aws_vpc.todo_team_vpc.id
  cidr_block        = cidrsubnet(var.public_subnet_cidr, 4, count.index)
  availability_zone = var.availability_zones[count.index]
  map_public_ip_on_launch = true # Public subnets need public IPs for Internet Gateway access

  tags = {
    Name = "${var.name_prefix}-public-subnet-${count.index + 1}"
  }
}

resource "aws_subnet" "todo_private_subnet" {
  count             = length(var.availability_zones)
  vpc_id            = aws_vpc.todo_team_vpc.id
  cidr_block        = cidrsubnet(var.private_subnet_cidr, 4, count.index)
  availability_zone = var.availability_zones[count.index]
  # Private subnets should NOT map public IPs

  tags = {
    Name = "${var.name_prefix}-private-subnet-${count.index + 1}"
  }
}


resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.todo_team_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.todo_igw.id
  }

  tags = {
    Name = "${var.name_prefix}-public-rt"
  }
}

# Associate Public Subnets with Public Route Table
resource "aws_route_table_association" "public_assoc" {
  count          = length(aws_subnet.todo_public_subnet)
  subnet_id      = aws_subnet.todo_public_subnet[count.index].id
  route_table_id = aws_route_table.public_rt.id
}



# Elastic IP for NAT Gateway
resource "aws_eip" "nat_gateway_eip" {
  domain = "vpc" # Associates the EIP with a VPC

  tags = {
    Name = "${var.name_prefix}-nat-eip"
  }
}

# NAT Gateway (placed in a Public Subnet)
resource "aws_nat_gateway" "main" {
  
  subnet_id     = aws_subnet.todo_public_subnet[0].id # Using the first public subnet for the NAT Gateway
  allocation_id = aws_eip.nat_gateway_eip.id           # Assigning the Elastic IP

  tags = {
    Name = "${var.name_prefix}-nat-gateway"
  }
  depends_on = [aws_internet_gateway.todo_igw] # Ensure IGW exists before NAT Gateway
}

# Private Route Table (routes to NAT Gateway for internet access)
resource "aws_route_table" "private_rt" {
  vpc_id = aws_vpc.todo_team_vpc.id

  # Local route for traffic within the VPC
  route {
    cidr_block = var.cidr_block 
    gateway_id = "local"
  }

  
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main.id # Reference the NAT Gateway created above
  }

  tags = {
    Name = "${var.name_prefix}-private-rt"
  }
}

# Associate Private Subnets with the Private Route Table
resource "aws_route_table_association" "private_assoc" {
  count          = length(aws_subnet.todo_private_subnet)
  subnet_id      = aws_subnet.todo_private_subnet[count.index].id
  route_table_id = aws_route_table.private_rt.id
}