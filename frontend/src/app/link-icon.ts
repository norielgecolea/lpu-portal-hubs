import { Component, input } from '@angular/core';

@Component({
  selector: 'app-link-icon',
  templateUrl: './link-icon.html',
})
export class LinkIcon {
  readonly icon = input.required<string>();
  readonly layout = input<'admin' | 'learner'>('admin');

  protected svgClass(): string {
    return this.layout() === 'learner'
      ? 'h-6 w-6 sm:h-8 sm:w-8 xl:h-9 xl:w-9'
      : 'h-8 w-8 xl:h-9 xl:w-9';
  }

  protected teamsClass(): string {
    return this.layout() === 'learner'
      ? 'h-7 w-7 object-contain sm:h-9 sm:w-9 xl:h-10 xl:w-10'
      : 'h-9 w-9 object-contain xl:h-10 xl:w-10';
  }

  protected erpClass(): string {
    return 'h-10 w-7 object-contain xl:h-11 xl:w-8';
  }

  protected doradoClass(): string {
    return 'h-7 w-12 object-contain xl:h-8 xl:w-14';
  }

  protected litClass(): string {
    return this.layout() === 'learner'
      ? 'h-7 w-7 object-contain sm:h-9 sm:w-9 xl:h-10 xl:w-10'
      : 'h-9 w-9 object-contain xl:h-10 xl:w-10';
  }
}
