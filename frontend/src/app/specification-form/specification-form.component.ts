import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SpecificationFile, SpecificationMetadata} from '../models/specificationfile';
import {ServiceStore} from '../service-store.service';
import {Service} from '../models/service';
import {ApiLanguage} from '../models/specification';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {animate, style, transition, trigger} from '@angular/animations';

const hiddenStyle = {
  'max-height': '0px',
  'visibility': 'hidden',
  'opacity': '0'
};

const visibleStyle = {
  'max-height': '300px',
  'visibility': 'visible',
  'opacity': '1'
};

@Component({
  selector: 'app-specification-form',
  templateUrl: './specification-form.component.html',
  styleUrls: ['./specification-form.component.css'],
  providers: [ServiceStore],
  animations: [
    trigger('displayFields', [
      transition(':enter', [
        style(hiddenStyle),
        animate('200ms ease-in', style(visibleStyle))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style(hiddenStyle))
      ])
    ])
  ]
})
export class SpecificationFormComponent implements OnInit {
  error: string;
  specificationFiles: File[];
  remoteFileUrl: string;
  additionalFields = {title: '', version: '', description: '', endpointUrl: ''};
  id?: string;
  showAdditionalMetadataFields = false;
  objectKeys = Object.keys;

  constructor(private router: Router,
              private serviceStore: ServiceStore,
              private route: ActivatedRoute,
              private modalService: NgbModal) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['serviceId']) {
        this.id = params['serviceId'];
      }
    });
  }

  private open(content) {
    this.showAdditionalMetadataFields = false;
    // Otherwise, metadata fields persist when the modal is closed and reopened,
    // even though the file is no longer selected.

    this.modalService.open(content, {}).result.then(
      () => {
        // modal is closed
        this.error = undefined;
      },
      () => {
        // modal is dismissed
        this.error = undefined;
      }
      );
  }

  public onLocalFileChange(event) {
    this.specificationFiles = event.target.files;
    this.showAdditionalMetadataFields = this.specificationFiles.length === 1 && /.*\.graphql/.test(this.specificationFiles[0].name);
  }

  public async submitLocalSpecification() {
    const allFieldsPresent: boolean = !!this.additionalFields.title && !!this.additionalFields.version;
    if (this.showAdditionalMetadataFields && !allFieldsPresent) {
      this.error = 'Title and version are required';
      return;
    }
    this.handleLocalFile();
  }

  public async submitRemoteSpecification() {
    this.handleRemoteFile();
  }

  private handleLocalFile() {
    if (!this.specificationFiles) {
      this.error = 'No file selected';
      return;
    }

    const mutableFileList = Array.from(this.specificationFiles);
    if (this.specificationFiles.length > 1 &&
      mutableFileList.some(file => /.*\.graphql/.test(file.name))) {
      this.error = 'Multi-upload is only allowed when every file is of OpenAPI format';
      return;
    }

    if (this.specificationFiles.length > 1) {
      const arrayOfPromises: Promise<SpecificationFile>[] =
        mutableFileList.map(file => this.createFileUploadPromise(file));

      const promiseOfArray: Promise<SpecificationFile[]> =
        Promise.all(arrayOfPromises);

      promiseOfArray.then(files => {
        // Once all the promises are fulfilled, we POST the specifications
        this.createSpecifications(files);
      }).catch(() =>
        this.error = 'Error during file read operation'
      );

    } else if (this.specificationFiles.length === 1) {
      const reader = new FileReader();
      const me = this;

      reader.onload = function () {
        const fileContent = reader.result.toString();
        const metadata: SpecificationMetadata = me.showAdditionalMetadataFields ?
          {...me.additionalFields, language: ApiLanguage.GraphQL}
          : null;
        const file = new SpecificationFile(fileContent, null, me.id, metadata);
        me.createSpecifications([file]);
      };

      reader.readAsText(this.specificationFiles[0]);
    }
  }

  private handleRemoteFile() {
    this.createSpecifications([
      new SpecificationFile(null, this.remoteFileUrl, this.id, null)
    ]);
  }

  private createSpecifications(files: SpecificationFile[]) {
    this.serviceStore.createSpecifications(files)
      .subscribe(event => {
          this.modalService.dismissAll();
          this.router.navigateByUrl('/');
        },
        error => this.error = error.error.userMessage);
  }

  createFileUploadPromise: (blob: Blob) => Promise<SpecificationFile> =
    (inputFile) => {
      const temporaryFileReader = new FileReader();

      return new Promise((resolve, reject) => {
        temporaryFileReader.onerror = () => {
          temporaryFileReader.abort();
          reject();
        };

        temporaryFileReader.onload = () => {
          const text = temporaryFileReader.result.toString();
          const dto = new SpecificationFile(text, null, this.id, null);
          resolve(dto);
        };

        temporaryFileReader.readAsText(inputFile, 'UTF-8');
      });
    }
}
