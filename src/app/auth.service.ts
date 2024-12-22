import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loginUrl = 'http://localhost:8080/rest/auth/login';
  private tokenKey = 'authToken';
  private roleKey = 'userRole';
  private currentUserRole = new BehaviorSubject<string | null>(this.getStoredRole());

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string, role: string): Observable<{ token: string, role: string }> {
    return this.http.post<{ token: string, role: string }>(this.loginUrl, { username, password, role })
      .pipe(
        tap(response => {
          // Save the token and role to localStorage or sessionStorage
          this.saveToken(response.token);
          this.saveRole(response.role);  // This line stores the role
          console.log('Role from response:', response.role);
        })
      );
  }



  register(registerData: { username: string; password: string; email: string; role: string }): Observable<any> {
    return this.http.post<any>('http://localhost:8080/rest/auth/register', registerData)
      .pipe(
        tap(response => {
          // If the response contains a token, save the token and role
          if (response.token && response.role) {
            this.saveToken(response.token);
            this.saveRole(response.role);
          }
        })
      );
  }



  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  saveRole(role: string): void {
    localStorage.setItem(this.roleKey, role);
    this.currentUserRole.next(role);
  }


  private getStoredRole(): string | null {
    console.log(localStorage.getItem(this.roleKey))
    return localStorage.getItem(this.roleKey);

  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getCurrentUserRole(): Observable<string | null> {
    return this.currentUserRole.asObservable();
  }


  isAdmin(): boolean {
    return this.getStoredRole() === 'admin';
  }

  isUser(): boolean {
    return this.getStoredRole() === 'user';
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    this.currentUserRole.next(null);
    this.router.navigate(['/login']);
  }
}
