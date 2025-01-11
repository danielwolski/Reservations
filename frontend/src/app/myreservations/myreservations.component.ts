import { Component, OnInit } from '@angular/core';
import { UserReservations } from '../models/event.model';
import { ReservationService } from '../services/event.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-myreservations',
  imports: [CommonModule],
  templateUrl: './myreservations.component.html',
  styleUrls: ['./myreservations.component.css']
})
export class MyReservationsComponent implements OnInit {
  reservations: UserReservations[] = [];
  errorMessage: string | null = null;

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    const username = localStorage.getItem('username');
    if (username) {
      this.getUserReservations(username);
    }
  }

  getUserReservations(username: string): void {
    this.reservationService.getUserReservations(username).subscribe({
      next: (data) => {
        this.reservations = data;
        this.errorMessage = null;
      },
      error: (err) => {
        this.errorMessage = 'Error during getting user reservations: ' + err.error;
      }
    });
  }
}
