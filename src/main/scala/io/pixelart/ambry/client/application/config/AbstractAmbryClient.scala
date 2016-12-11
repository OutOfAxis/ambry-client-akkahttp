package io.pixelart.ambry.client.application.config

/**
  * Created by rabzu on 11/12/2016.
  */
import java.io.File
import com.softwaremill.tagging._


class AbstractAmbryClient {


    /**
      * Check the status of the frontend
      * @return the response which contains code and status
      */
    def healthCheck: AmbryResponse

    /**
      * Get the file properties represented by the ambryId
      * @param ambryId the file's ambryId
      * @return file's info of the given resource id
      */
    def getFileProperty(ambryId: String @@ AmbryId): AmbryBlobInfoResponse

    /**
      * Get the file's user metadata represented by the ambryId
      * @param ambryId the file's ambryId
      * @return file's user metadata of the given resource id
      */
    def getFileUserMetadata(ambryId: String @@ AmbryId): AmbryUMResponse

    /**
      * Get the file with the given resource id
      * @param ambryId the file's ambryId
      * @return the file object
      */
    def getFile(ambryId: String @@ AmbryId): AmbryGetFileResponse

    /**
      * Get the file with given resource id, and put into the local file object.
      * @param ambryId the resource id
      * @param localFile save the download file as local
      * @return the file object and result
      */
    def getFile(ambryId: String, localFile: File): AmbryGetFileResponse

    /**
      * Upload a file with the file's path.
      * @param filePath file's path at local.
      * @param fileType file type string, e.g.: "text/plain", "image/jpg"
      * @return information with save status and the resource id, i.e. ambryId, response from Amby
      */
    def postFile(filePath: String, fileType: String):AmbryPostFileResponse

    /**
      * Upload a file
      * @param file the file which will be uploaded
      * @param fileType file type string, e.g.: "text/plain", "image/jpg"
      * @return save status and the resource id response from Ambry
      */
     def postFile( file: File, fileType: String):AmbryPostFileResponse

    /**
      * Upload a file with bytes
      * @param fileBytes the file bytes which will be uploaded
      * @param fileType file type string, e.g.: "text/plain", "image/jpg"
      * @return save status and the resource id response from Ambry
      */

     def postFile(fileBytes: ByteSource, fileType: String): AmbryPostFileResponse

    /**
      * Delete a file with a given resource Id
      * @param ambryId resource id of the target file
      * @return remove status
      */
    def deleteFile(ambryId: String @@ AmbryId): AmbryBaseResponse



}
