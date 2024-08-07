# Configure the AWS Provider
provider "aws" {
  region = "us-east-1"
}

# Create a VPC
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "main-vpc"
  }
}

# Create a public subnet
resource "aws_subnet" "public" {
  vpc_id     = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
  map_public_ip_on_launch = true
  availability_zone = "us-east-1a"  # Adjust if needed
  tags = {
    Name = "public-subnet"
  }
}

# Create an Internet Gateway
resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id
  tags = {
    Name = "main-igw"
  }
}

# Create a Route Table
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }

  tags = {
    Name = "public-route-table"
  }
}

# Associate the Route Table with the Subnet
resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# Create a Security Group
resource "aws_security_group" "allow_ssh" {
  name        = "allow_ssh"
  description = "Allow SSH inbound traffic"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 9000
    to_port     = 9000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "allow-ssh-sg"
  }
}

# Define EC2 Instances
resource "aws_instance" "jenkins" {
  ami             = "ami-04a81a99f5ec58529" # Ubuntu 24.04 LTS AMI
  instance_type   = "t2.medium"
  key_name        = var.key_name
  vpc_security_group_ids = [aws_security_group.allow_ssh.id]
  subnet_id       = aws_subnet.public.id

  user_data = file("${path.module}/script-jenkins.sh")

  tags = {
    Name = "JenkinsServer"
  }
}

resource "aws_instance" "sonarqube" {
  ami             = "ami-04a81a99f5ec58529" # Ubuntu 24.04 LTS AMI
  instance_type   = "t2.medium"
  key_name        = var.key_name
  vpc_security_group_ids = [aws_security_group.allow_ssh.id]
  subnet_id       = aws_subnet.public.id

  user_data = file("${path.module}/script-sonarqube.sh")

  tags = {
    Name = "SonarQubeServer"
  }
}

resource "aws_instance" "nexus" {
  ami             = "ami-04a81a99f5ec58529" # Ubuntu 24.04 LTS AMI
  instance_type   = "t2.medium"
  key_name        = var.key_name
  vpc_security_group_ids = [aws_security_group.allow_ssh.id]
  subnet_id       = aws_subnet.public.id

  user_data = file("${path.module}/script-nexus.sh")

  tags = {
    Name = "NexusServer"
  }
}

# Outputs
output "public_subnet_id" {
  value = aws_subnet.public.id
}

output "jenkins_public_ip" {
  value = aws_instance.jenkins.public_ip
}

output "sonarqube_public_ip" {
  value = aws_instance.sonarqube.public_ip
}

output "nexus_public_ip" {
  value = aws_instance.nexus.public_ip
}
