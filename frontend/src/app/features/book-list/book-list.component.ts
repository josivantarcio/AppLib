import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { BookFormComponent } from '../book-form/book-form.component';
import { BookDetailComponent } from '../book-detail/book-detail.component';
import { BookService } from '../../core/services/book.service';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {
  displayedColumns: string[] = ['title', 'author', 'isbn', 'status', 'actions'];
  dataSource: MatTableDataSource<any>;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private dialog: MatDialog,
    private bookService: BookService
  ) {
    this.dataSource = new MatTableDataSource();
  }

  ngOnInit(): void {
    this.loadBooks();
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  private loadBooks(): void {
    // TODO: Implement service call to get real data
    const mockBooks = [
      { id: 1, title: 'Book 1', author: 'Author 1', isbn: '1234567890', status: 'AVAILABLE' },
      { id: 2, title: 'Book 2', author: 'Author 2', isbn: '0987654321', status: 'BORROWED' },
      { id: 3, title: 'Book 3', author: 'Author 3', isbn: '1122334455', status: 'AVAILABLE' }
    ];
    this.dataSource.data = mockBooks;
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openAddBookDialog(): void {
    const dialogRef = this.dialog.open(BookFormComponent, {
      width: '600px',
      data: { mode: 'add' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadBooks();
      }
    });
  }

  viewBook(book: any): void {
    this.dialog.open(BookDetailComponent, {
      width: '800px',
      data: { book }
    });
  }

  editBook(book: any): void {
    const dialogRef = this.dialog.open(BookFormComponent, {
      width: '600px',
      data: { mode: 'edit', book }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadBooks();
      }
    });
  }

  deleteBook(book: any): void {
    // TODO: Implement delete functionality
    console.log('Delete book:', book);
  }
} 