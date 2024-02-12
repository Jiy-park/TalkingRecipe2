package com.dd2d.talkingrecipe2.view.sub_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dd2d.talkingrecipe2.data_struct.RecipePost
import com.dd2d.talkingrecipe2.ui.PostViewer
import com.dd2d.talkingrecipe2.ui.fillWidthOfParent
import com.dd2d.talkingrecipe2.ui.theme.HintText
import com.dd2d.talkingrecipe2.ui.theme.kotex

/** @param emptyComment [postList]가 비어있을 경우 유저에게 보여질 문장.*/
@Composable
fun PostListView(
    modifier: Modifier = Modifier,
    emptyComment: String,
    postList: List<RecipePost>,
    onClickPost: (post: RecipePost)->Unit,
){
    LazyColumn(
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(15.dp),
        modifier = modifier.fillMaxSize()
    ){
        if(postList.isEmpty()){
            item {
                kotex(text = emptyComment)
            }
        }
        items(
            items = postList,
            key = { post -> post.recipeBasicInfo.recipeId }
        ){ post->
            PostViewer(post = post) { clicked-> onClickPost(clicked) }
            Spacer(modifier = modifier.height(15.dp))
            Divider(modifier = modifier.fillWidthOfParent(15.dp), color = HintText)
            Spacer(modifier = modifier.height(15.dp))
        }
    }
}
