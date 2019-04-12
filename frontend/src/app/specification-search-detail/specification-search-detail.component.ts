import {Component, OnInit} from '@angular/core';
import {Service} from '../models/service';
import {ServiceStore} from '../service-store.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-specification-search-detail',
  templateUrl: './specification-search-detail.component.html',
  styleUrls: ['./specification-search-detail.component.css'],
  providers: [ServiceStore]
})
export class SpecificationSearchDetailComponent implements OnInit {
  services: Service[];
  searchString: string;

  constructor(private serviceStore: ServiceStore, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.serviceStore.searchForServices(params['searchString'])
        .subscribe((data: Service[]) => this.services = data);
      this.searchString = params['searchString'];
    });
  }

  public async searchSpecifications(event) {
    if (event.keyCode === 13) {
      this.serviceStore.searchForServices(this.searchString).subscribe((data: Service[]) => this.services = data);
    }
  }

}
