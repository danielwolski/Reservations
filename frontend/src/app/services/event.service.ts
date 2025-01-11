import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, tap } from 'rxjs';
import { CreateReservation, DailyReservations, UserReservations } from '../models/event.model';
import { AuthService } from '../authorization/auth.service';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private apiUrl = 'http://localhost:8080/api/reservations';

  private reservationsUpdatedSubject = new Subject<void>();

  reservationsUpdated$ = this.reservationsUpdatedSubject.asObservable();

  constructor(private authService: AuthService, private http: HttpClient) {}


  getEventDetailsByDay(day: string): Observable<DailyReservations> | null {
    if (day != '' && day != null) {
      return this.http.get<DailyReservations>(`${this.apiUrl}/${day}`);
    } else {
      return null;
    }
  }

  getUserReservations(username: string): Observable<UserReservations[]> {
      return this.http.get<UserReservations[]>(`${this.apiUrl}/user/${username}`);
  }

  addReservation(request: CreateReservation, date: string): Observable<CreateReservation> {
    const username = this.authService.getUsername();
    const reservationRequestToSend = {
      ...request,
      username: username,
      date: date
    };

    return this.http.post<any>(this.apiUrl, reservationRequestToSend);
  }

  removeEvent(eventId: number): Observable<void> {
    const url = `${this.apiUrl}/${eventId}`;
    return this.http.delete<void>(url).pipe(
      tap(() => this.reservationsUpdatedSubject.next())
    );
  }
}
