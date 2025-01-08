import { Component } from '@angular/core';
import { AuthService } from '../authorization/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  username = '';
  password = '';
  email = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onRegister() {
    this.authService.login(this.username, this.password).subscribe({
      next: response => {
        this.authService.saveToken(response.token);
        this.authService.saveUsername(this.username);
        this.router.navigate(['/events-list']);
      },
      error: err => {
        this.username = '';
        this.password = '';
        this.errorMessage = err.error;
      }
    });
  }

  goToLogin() {
    console.log("registergoto");
    this.router.navigate(['/login']);
  }
}
