import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  publisher: string;
  publicationYear: string;
  genre: string;
  description: string;
  coverImage: string;
  status: 'AVAILABLE' | 'LOANED';
  loans?: Loan[];
}

export interface Loan {
  id: number;
  bookId: number;
  userId: number;
  userName: string;
  loanDate: string;
  dueDate: string;
  returnDate?: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private apiUrl = `${environment.apiUrl}/books`;

  constructor(private http: HttpClient) { }

  getBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.apiUrl);
  }

  getBook(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/${id}`);
  }

  createBook(book: Partial<Book>): Observable<Book> {
    return this.http.post<Book>(this.apiUrl, book);
  }

  updateBook(id: number, book: Partial<Book>): Observable<Book> {
    return this.http.put<Book>(`${this.apiUrl}/${id}`, book);
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  loanBook(bookId: number, userId: number, dueDate: string): Observable<Loan> {
    return this.http.post<Loan>(`${this.apiUrl}/${bookId}/loans`, {
      userId,
      dueDate
    });
  }

  returnBook(bookId: number, loanId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${bookId}/loans/${loanId}/return`, {});
  }
} 