import { Injectable } from '@angular/core';
import { Api } from '../api';
import { generatePresignedUrl, getPresignedUrlToDelete } from '../functions';
import { PreSignedUrlDto } from '../models';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class FileService {


  presignedUrlDto:PreSignedUrlDto | undefined;

  constructor(
    private api:Api,
    private http:HttpClient,
    private toastr:ToastrService
  ){}

  //get presigned url
  async getPreSignedUrl(
    folder:string,
    fileName: string,
    contentType: string
  ): Promise<PreSignedUrlDto> {

    return await this.api.invoke(generatePresignedUrl, {
      body: {
        folder: folder,
        fileName: fileName,
        contentType: contentType
      }
    });
  }

  //uplod to the cloud
 uploadFileToCloud(
    preSignedUrl: string,
    file: File
  ) {
    return this.http.put(preSignedUrl, file, {
      headers: {
        'Content-Type': file.type,
        'x-amz-acl': 'public-read'
      },
      observe: 'events',
      reportProgress: true
    });
  }


  // get presigned url to delete
  async getPresignedUrlForDelete( fileName: string, folder: string): Promise<string> {

    const response = await this.api.invoke(
      getPresignedUrlToDelete,
      {
        file_name: fileName,
        folder: folder
      }
    );

    return response.preSignedUrl as string;
  }

  //delete file
  async deleteFile(preSignedUrl:string){
     const deleteResponse = await firstValueFrom(
        this.http.delete(preSignedUrl,{
           observe: 'response'
        })
     );

     if(deleteResponse.status === 200){
       this.toastr.success("File deleted successfully");
     }
  }

  
}
