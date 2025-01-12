import {Component, OnInit, Output} from '@angular/core';
import {Specification} from '../models/specification';
import {ActivatedRoute} from '@angular/router';
import {SpecificationStore} from '../specification-store.service';

@Component({
  selector: 'app-specification-view',
  templateUrl: './specification-view.component.html',
  providers: [SpecificationStore]
})

export class SpecificationViewComponent implements OnInit {
  @Output() specification: Specification;
  error: string;

  constructor(protected route: ActivatedRoute, protected specificationStore: SpecificationStore) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.specificationStore.getSpecification(params['serviceId'], params['version'])
        .subscribe(data => {
          this.specification = data;
        },
      error => this.error = error.error.userMessage);
    });
  }
}
