export interface DailyReservations {
  date: string;
  tableReservationSlots: TableReservationSlots[];
}

export interface TableReservationSlots {
  tableId: number;
  reservationSlots: ReservationSlot[];
}

export interface ReservationSlot {
  startTime: string;
  available: boolean;
}

export interface CreateReservation {
    username: string;
    tableId: number;
    date: string;
    slotStartTimes: string[];
}

export interface UserReservations {
  slotsIds: number[];
  date: string;
  startTime: string;
  endTime: string;
  tableId: number;
}
