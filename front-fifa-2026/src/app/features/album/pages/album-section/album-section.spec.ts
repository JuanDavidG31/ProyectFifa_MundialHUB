import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlbumSection } from './album-section';

describe('AlbumSection', () => {
  let component: AlbumSection;
  let fixture: ComponentFixture<AlbumSection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumSection]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AlbumSection);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
