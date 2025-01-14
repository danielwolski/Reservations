import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableReservationComponent } from './tablereservation.component';

describe('ModalComponent', () => {
  let component: TableReservationComponent;
  let fixture: ComponentFixture<TableReservationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableReservationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TableReservationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
