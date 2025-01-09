import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private loginUrl = 'http://localhost:8080/api/users/login';
  private registerUrl = 'http://localhost:8080/api/users/register';
  private logoutUrl = 'http://localhost:8080/api/users/logout';

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(this.loginUrl, { username, password });
  }

  register(email: string, username: string, password: string): Observable<any> {
    return this.http.post<any>(this.registerUrl, { email, username, password });
  }

  saveToken(token: string): void {
    localStorage.setItem('jwtToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  saveUsername(username: any) {
    localStorage.setItem('username', username);
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  logout(): void {
    localStorage.removeItem('jwtToken');

    //TODO fix on backend side
    // this.http.post(this.logoutUrl, {}).subscribe({
    //   next: () => console.log('Logged out on the server'),
    //   error: (err) => console.error('Server logout error:', err),
    // });
  }
}
