package com.dd2d.talkingrecipe2.view.sub_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dd2d.talkingrecipe2.navigation.SubScreenDestination
import com.dd2d.talkingrecipe2.ui.theme.MainColor
import com.dd2d.talkingrecipe2.ui.theme.SubColor
import com.dd2d.talkingrecipe2.ui.theme.kotex

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubScreenCenterView(
    modifier: Modifier = Modifier,
    currentTab: Int,
    onTabChange: (tabIndex: Int)->Unit,
    mainContent: @Composable ()->Unit,
){
    TabRow(
        selectedTabIndex = currentTab,
        containerColor = Color.White,
        contentColor = MainColor,
        indicator = { positions->
            TabRowDefaults.Indicator(
                height = 3.dp,
                color = MainColor,
                modifier = modifier
                    .tabIndicatorOffset(positions[currentTab])
            )
        },
        divider = { TabRowDefaults.Indicator(height = 2.dp, color = SubColor) },
    ) {
        SubScreenDestination.values().forEachIndexed { index, destination->
            Tab(
                selected = currentTab == index,
                onClick = { onTabChange(index) },
                modifier = modifier.height(50.dp)
            ) {
                kotex(
                    text = destination.description,
                    size = 15.sp
                )
            }
        }
    }

    mainContent()
}