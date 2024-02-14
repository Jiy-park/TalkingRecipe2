package com.dd2d.talkingrecipe2

import android.net.Uri
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/** 이미지 업로드용 함수. 이미지 업로드 시 공용으로 사용.
 * 내부적으로 갤러리에서 온 이미지인지, 서버에서 온 이미지인지 [Uri.isFromServer] 사용하여 판단 후 각 상황에 맞게 이미지를 변환하여 업로드한다.
 *- 갤러리로부터 온 경우 -> [Uri]형태로 올린다.
 *- 서버로부터 온 경우 -> [Uri.toByteArray] 사용하여 [ByteArray] 형태로 올린다.
 * @param bucketApi 이미지를 업로드할 스토리지의 [BucketApi]. [BucketApi]를 생성할 때 [uploadPath] 직전 경로까지 입력한다.
 * ex>이미지 저장 경로 : images/{user_id}/profile.jpeg 일 때, [BucketApi]의 경로는 images/{user_id}까지 되어야 한다.
 * @param imageUri 스토리지에 업로드할 이미지의 uri.
 * @param uploadPath 이미지가 업로드 될 때의 이름. 확장자를 포함하여 적는다. ex> "{recipe_id}_thumbnail.jpeg"
 * @param onTask 업로드의 시작과 끝을 람다 콜백을 통해 전달해준다.
 * @param callFrom [uploadImage]가 어디서 호출된 건지 알려준다. [onTask]애서 또는 [Exception]이 발생했을 경우 [callFrom]의 값을 출력해준다.
 * ex> A()로부터 호출된 경우 [callFrom] = "A" -> onTask("call from A()")
 *
 * @throws IOException*/
suspend fun uploadImage(
    bucketApi: BucketApi,
    imageUri: Uri,
    uploadPath: String,
    callFrom: String,
    onTask: (msg: String)->Unit,
){
    withContext(Dispatchers.IO){
        try {
            onTask("uploadImage from $callFrom()::start upload image. path -> $uploadPath.")
            if(imageUri.isFromServer()){
                bucketApi.upload(data = imageUri.toByteArray(), path = uploadPath, upsert = true)
            }
            else{
                bucketApi.upload(uri = imageUri, path = uploadPath, upsert = true)
            }
            onTask("uploadImage from $callFrom()::finished upload image. path -> $uploadPath.")
        }
        catch (e: Exception){
            throw IOException("IOException in uploadImage from $callFrom().\n" +
                    "imageUri: $imageUri, uploadPath: $uploadPath\n" +
                    "error: $e.")
        }
    }
}