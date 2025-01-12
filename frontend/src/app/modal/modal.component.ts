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

  errorMessage = '';

  reservations: DailyReservations | undefined;
  isAddEventFormVisible: boolean = false;
  selectedSlots: { [tableId: number]: Set<string> } = {};
  selectedTableId: number | null = null;

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
    if (this.selectedTableId && this.selectedTableId !== tableId) {
      return;
    }
  
    if (!this.selectedSlots[tableId]) {
      this.selectedSlots[tableId] = new Set<string>();
    }
  
    if (this.selectedSlots[tableId].has(startTime)) {
      this.selectedSlots[tableId].delete(startTime);
  
      if (this.selectedSlots[tableId].size === 0) {
        delete this.selectedSlots[tableId];
        this.selectedTableId = null;
      }
    } else {
      this.selectedSlots[tableId].add(startTime);
      this.selectedTableId = tableId;
    }
  }
  

  close(): void {
    this.closeModal.emit();
    this.selectedSlots = {};
    this.selectedTableId = null;
    this.errorMessage = '';
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
        this.eventService.addReservation(reservation, this.selectedDate).subscribe({
          next: response => {
            this.close(); 
          },
          error: err => {
            console.log(err.error)
            this.errorMessage = err.error;
          }
       });
      });
    }
    
    this.selectedSlots = {};
    this.selectedTableId = null;
  }
  

  loadReservationsForADay(): void {
    this.eventService.getEventDetailsByDay(this.selectedDate)?.subscribe((data: DailyReservations) => {
      this.reservations = data;
    });
  }

  getTime(dateTimeString: String) {
    return dateTimeString.split('T')[1].split(':')[0] + ":" + dateTimeString.split('T')[1].split(':')[1];
  }

  isSlotSelected(tableId: number, startTime: string): boolean {
    return this.selectedSlots[tableId]?.has(startTime) || false;
  }
}
