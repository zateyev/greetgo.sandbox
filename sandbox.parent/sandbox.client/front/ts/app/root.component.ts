import {Component} from "@angular/core";

@Component({
  selector: 'root-component',
  template: `
    <div>
      Hello from {{name}}
    </div>
  `
})
export class RootComponent {
  name: string = "Root Component"
}
