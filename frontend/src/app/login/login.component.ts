import { Component } from '@angular/core';
import { AuthService } from '../authorization/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.authService.login(this.username, this.password).subscribe({
      next: response => {
        this.authService.saveToken(response.token);
        this.authService.saveUsername(this.username);
        this.router.navigate(['/calendar']);
      },
      error: err => {
        this.username = '';
        this.password = '';
        this.errorMessage = err.error;
      }
    });
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}
