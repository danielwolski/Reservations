import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservationService } from '../services/event.service';
import { CreateReservation, DailyReservations } from '../models/event.model';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css'],
  imports: [
    CommonModule
  ],
  standalone: true
})
export class ModalComponent implements OnInit, OnChanges {
  @Input() isVisible: boolean = false; 
  @Input() selectedDate: string = '';  
  @Output() closeModal: EventEmitter<void> = new EventEmitter(); 

  reservations: DailyReservations | undefined;
  isAddEventFormVisible: boolean = false;
  selectedSlots: { [tableId: number]: Set<string> } = {};

  constructor(private eventService: ReservationService
  ) {}

  ngOnInit(): void {
    this.eventService.reservationsUpdated$.subscribe(() => {
      this.loadReservationsForADay();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isVisible'] && changes['isVisible'].currentValue === true) {
      this.loadReservationsForADay();
    }
  }

  toggleSlotSelection(tableId: number, startTime: string): void {
    if (!this.selectedSlots[tableId]) {
      this.selectedSlots[tableId] = new Set<string>();
    }
  
    if (this.selectedSlots[tableId].has(startTime)) {
      this.selectedSlots[tableId].delete(startTime);
    } else {
      this.selectedSlots[tableId].add(startTime);
    }
  
    console.log('Selected slots:', this.selectedSlots);
  }

  close(): void {
    this.closeModal.emit();
    this.selectedSlots = {};
  }

  createReservation(): void {
    const reservations: CreateReservation[] = [];
  
    for (const tableId in this.selectedSlots) {
      if (this.selectedSlots.hasOwnProperty(tableId)) {
        const slotStartTimes = Array.from(this.selectedSlots[tableId]);
        if (slotStartTimes.length > 0) {
          reservations.push({
            username: '', 
            tableId: parseInt(tableId, 10),
            date: this.selectedDate,
            slotStartTimes: slotStartTimes,
          });
        }
      }
    }
  
    if (reservations.length > 0) {
      reservations.forEach((reservation) => {
        this.eventService.addReservation(reservation, this.selectedDate).subscribe(
          response => {
            console.log("Reservation successfully added", response);
          },
          error => {
            console.error("Error while making reservation request", error);
          }
        );
      });
    }
    
    this.selectedSlots = {};
    this.close(); 
  }
  

  loadReservationsForADay(): void {
    this.eventService.getEventDetailsByDay(this.selectedDate)?.subscribe((data: DailyReservations) => {
      this.reservations = data;
      console.log('Reservations:', this.reservations);
      console.log('Type:', typeof this.reservations);
    });
  }

  getTime(dateTimeString: String) {
    return dateTimeString.split('T')[1].split(':')[0] + ":" + dateTimeString.split('T')[1].split(':')[1];
  }

  isSlotSelected(tableId: number, startTime: string): boolean {
    return this.selectedSlots[tableId]?.has(startTime) || false;
  }
}
