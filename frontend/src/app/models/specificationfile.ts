interface SpecificationMetaData {
  title: string;
  version: string;
  description: string;
}

export class SpecificationFile {
  metaData?: SpecificationMetaData;
  fileContent: string;
  fileUrl: string;

  constructor(fileContent: string, fileUrl: string, metaData?: SpecificationMetaData) {
    this.fileContent = fileContent;
    this.fileUrl = fileUrl;
    if (metaData) {
      this.metaData = metaData;
    }
  }
}
