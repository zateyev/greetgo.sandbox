import {Component, EventEmitter, Output} from "@angular/core";

@Component({
  selector: 'main-form-component',
  template: `
    <div>
      Main Form Component
      <button (click)="exit.emit()">Выход</button>
    </div>`,
})
export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();
}
