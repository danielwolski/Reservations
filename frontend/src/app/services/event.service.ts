import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { catchError, Observable, of, Subject, tap } from 'rxjs';
import { CreateReservation, DailyReservations } from '../models/event.model';
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
