import {Component} from "@angular/core";

@Component({
  selector: 'client-record-component',
  template: require('./client-record-component.html'),
  styles: [require('./client-record-component.css')],
})
// Взято с https://embed.plnkr.co/7kqyiW97CI696Ixn020g/
export class ClientRecordComponent {
  public isVisible = false;
  private isAnimating = false;

  constructor() {}

  show() {
    this.isVisible = true;
    setTimeout(() => this.isAnimating = true, 100);
  }

  hide() {
    this.isAnimating = false;
    setTimeout(() => this.isVisible = false, 300);
  }

  cancelClientRecordEdit() {
    this.hide();
  }

  onContainerClicked(event: MouseEvent) {
    if ((<HTMLElement>event.target).classList.contains('modal')) {
      //this.hide();
    }
  }
}