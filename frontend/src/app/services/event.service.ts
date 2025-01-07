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

  addReservation(request: CreateReservation, date: string): Observable<CreateReservation | null> {
    const username = this.authService.getUsername();
    const reservationRequestToSend = {
      ...request,
      username: username,
      date: date
    };
    console.log("Sent reservation request: ", reservationRequestToSend);

    return this.http.post<CreateReservation>(this.apiUrl, reservationRequestToSend).pipe(
      tap(response => console.log("Received response:", response)),
      catchError(error => {
        console.error("Error occurred while making reservation request", error);
        return of(null); 
      })
    );
  }

  removeEvent(eventId: number): Observable<void> {
    const url = `${this.apiUrl}/${eventId}`;
    return this.http.delete<void>(url).pipe(
      tap(() => this.reservationsUpdatedSubject.next())
    );
  }
}
