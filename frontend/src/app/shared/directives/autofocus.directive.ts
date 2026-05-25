import { Directive, ElementRef, OnInit, inject } from '@angular/core';

@Directive({
  selector: '[appAutofocus]',
  standalone: true
})
export class AutofocusDirective implements OnInit {
  private readonly el = inject(ElementRef<HTMLElement>);

  ngOnInit(): void {
    setTimeout(() => this.el.nativeElement.focus(), 0);
  }
}
