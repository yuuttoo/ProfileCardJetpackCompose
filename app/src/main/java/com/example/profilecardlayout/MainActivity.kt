package com.example.profilecardlayout

import android.os.Bundle
import android.service.autofill.OnClickAction
import android.text.style.IconMarginSpan
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.example.profilecardlayout.ui.theme.ProfileCardLayoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileCardLayoutTheme() {
                UserAppication()
            }

        }
    }
}



@Composable
fun AppBar(title: String, icon: ImageVector, iconClickAction: () -> Unit) {
    TopAppBar(
            navigationIcon = {
              Icon(
                  imageVector = icon,
                  contentDescription = "Top Bar icon",
                  modifier = Modifier.padding(12.dp)
                      .clickable { iconClickAction.invoke() }
              )
            },
        title = { Text((title))}
    )
}

//頁面加換頁總管
@Composable
fun UserAppication(userProfile: List<UserProfile> = userProfileList) {
    val navController = rememberNavController()
    //NavHost為nav route的總管 這裡叫做graph,  is like a map for navigate
    NavHost(navController = navController, startDestination = "user_list") { //user_list為首頁

        composable("user_list") {
            UserListScreen(userProfileList, navController)
        }
        composable(
            route = "user_details/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType//id的type為Int
        })
        ){ navBackStackEntry ->
            UserProfileDetailScreen(navBackStackEntry.arguments!!.getInt("userId"), navController)

        }
    }
}


@Composable
fun UserListScreen(userProfiles: List<UserProfile>, navController: NavHostController?) {//做為首頁 用NavHostController
    Scaffold(topBar = { AppBar(
        title = "user_list",
        icon = Icons.Default.ArrowBack
        ) { }// 沒有click event 但參數規定要有 放個空白lambda
    }) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            ) {
            LazyColumn {
                items(userProfiles) { userProfile ->
                    ProfileCard(userProfile) {
                        //click event here
                        navController?.navigate("user_details/${userProfile.id}")//按下時 跳轉到user_details頁面

                    }
                }
                
            }

        }
    }
}


@Composable
fun UserProfileDetailScreen(userId: Int, navController: NavHostController?) {
    //check id
    val userProfile = userProfileList.first  { userProfile -> userId == userProfile.id}
    Scaffold(topBar = {
        AppBar(
        "User Profile Details",
        icon = Icons.Default.ArrowBack
        ) {//回上一頁的method
            navController?.navigateUp()//navigateUp 回到最近開啟的一頁
        }
    }) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {//靠左
                ProfilePicture(userProfile.pictureUrl, userProfile.status, 240.dp)
                ProfileContent(userProfile.name, userProfile.status, Alignment.CenterHorizontally)
            }
        }
    }
}







@Composable
fun ProfileCard(userProfile: UserProfile, clickAction: () -> Unit) {//埋一個clickAction作為可按的區域
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)//因為不知道高度
            .clickable { clickAction.invoke() } ,
    elevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {//靠左
            ProfilePicture(userProfile.pictureUrl, userProfile.status, 72.dp)
            ProfileContent(userProfile.name, userProfile.status, Alignment.Start)
        }

    }
}


@Composable
fun  ProfilePicture(pictureUrl: String, onLineStatus: Boolean, imageSize: Dp ) {
    Card (
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = if (onLineStatus) Color.Green
                else Color.Red
        ),
        modifier = Modifier
            .padding(16.dp)
            .size(imageSize),
        elevation = 4.dp
    ) {
        Image(
            painter = rememberImagePainter(data = pictureUrl,
            builder = {
                transformations(CircleCropTransformation())
            }),
            contentDescription =  "profile",
            modifier = Modifier.size(72.dp)
        )

    }

}


@Composable
fun  ProfileContent(userName: String, onLineStatus: Boolean, alignment: Alignment.Horizontal) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = alignment
    ) {
        CompositionLocalProvider(LocalContentAlpha provides
                if(onLineStatus) 1f
                else ContentAlpha.medium) {

            Text(
                userName,
                style = MaterialTheme.typography.h5)

        }
        //CompositionLocalProvider Composable 之間互相傳遞資料的方式
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                if(onLineStatus) "Active now"
                else "Offline",
                style = MaterialTheme.typography.body2)
        }

    }

}


@Preview(showBackground = true)
@Composable
fun UserPrfileDetailPreview() {//這裡是Preview所以還是先用UserProfileDetailScreen 因為ui預覽無法實作跳轉效果
    ProfileCardLayoutTheme {
        UserProfileDetailScreen(userId = 0, null)//preview而已 0擋著
    }
}


@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    ProfileCardLayoutTheme() {
        UserListScreen(userProfiles = userProfileList, null)
    }
}