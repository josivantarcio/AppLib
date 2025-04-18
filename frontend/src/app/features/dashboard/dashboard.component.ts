import { Component, OnInit } from '@angular/core';
import { BookService } from '../../core/services/book.service';
import { LoanService } from '../../core/services/loan.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  totalBooks: number = 0;
  availableBooks: number = 0;
  totalLoans: number = 0;
  activeLoans: number = 0;
  totalUsers: number = 0;
  recentLoans: any[] = [];
  popularBooks: any[] = [];

  constructor(
    private bookService: BookService,
    private loanService: LoanService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    // TODO: Implement service calls to get real data
    this.totalBooks = 100;
    this.availableBooks = 75;
    this.totalLoans = 200;
    this.activeLoans = 25;
    this.totalUsers = 50;
    
    // Mock data for recent loans
    this.recentLoans = [
      { book: 'Book 1', user: 'User 1', date: new Date() },
      { book: 'Book 2', user: 'User 2', date: new Date() },
      { book: 'Book 3', user: 'User 3', date: new Date() }
    ];

    // Mock data for popular books
    this.popularBooks = [
      { title: 'Book 1', loans: 15 },
      { title: 'Book 2', loans: 12 },
      { title: 'Book 3', loans: 10 }
    ];
  }
} 