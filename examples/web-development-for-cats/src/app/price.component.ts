import {Component, Input} from '@angular/core';
import {Section} from "./app.component";

@Component({
  selector: 'my-price',
  templateUrl: './price.component.html'
})
export class PriceComponent {
  @Input() data: Section
}
