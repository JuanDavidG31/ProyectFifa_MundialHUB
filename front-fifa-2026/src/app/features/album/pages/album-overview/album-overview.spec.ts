import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlbumOverview } from './album-overview';

describe('AlbumOverview', () => {
  let component: AlbumOverview;
  let fixture: ComponentFixture<AlbumOverview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumOverview]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AlbumOverview);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
