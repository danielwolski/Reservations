import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserReservations } from '../models/event.model';
import { ReservationService } from '../services/event.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-myreservations',
  imports: [CommonModule],
  templateUrl: './myreservations.component.html',
  styleUrls: ['./myreservations.component.css']
})
export class MyReservationsComponent implements OnInit, OnDestroy {
  reservations: UserReservations[] = [];
  errorMessage: string | null = null;
  private subscriptions: Subscription = new Subscription();

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    const username = localStorage.getItem('username');
    if (username) {
      this.getUserReservations(username);

      this.subscriptions.add(
        this.reservationService.reservationsUpdated$.subscribe(() => {
          this.getUserReservations(username);
        })
      );
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

  cancelReservation(reservation: UserReservations): void {
    this.reservationService.cancelReservation(reservation.slotsIds).subscribe({
      next: () => {
        this.errorMessage = null;
      },
      error: (err) => {
        this.errorMessage = 'Error during canceling reservation: ' + err.error;
      }
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
