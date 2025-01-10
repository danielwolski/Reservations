import { Routes } from '@angular/router';
import { CalendarComponent } from './calendar/calendar.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './authorization/auth.guard';
import { RegisterComponent } from './register/register.component';
import { MyReservationsComponent } from './myreservations/myreservations.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'events-list', component: CalendarComponent, canActivate: [AuthGuard] },
  { path: 'myreservations', component: MyReservationsComponent, canActivate: [AuthGuard] },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];
