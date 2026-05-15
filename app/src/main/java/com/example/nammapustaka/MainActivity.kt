package com.example.nammapustaka

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableIntStateOf
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.horizontalScroll
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavHostController
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.zxing.integration.android.IntentIntegrator
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.nammapustaka.ui.theme.NammaPustakaTheme
import androidx.compose.runtime.LaunchedEffect
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


val bookList = mutableStateListOf<String>()
val categoryList = mutableStateListOf<String>()
var savedStudentName by mutableStateOf("")
var savedCollegeId by mutableStateOf("")
var savedPhone by mutableStateOf("")
var savedGmail by mutableStateOf("")
val borrowedBooks = mutableStateListOf<String>()
val dueDates = mutableStateListOf<String>()
var totalStudents by mutableIntStateOf(0)
val authorList = mutableStateListOf<String>()
val borrowedByList = mutableStateListOf<String>()
val ratingList = mutableStateListOf<String>()
val reviewList = mutableStateListOf<String>()

lateinit var bookDao: BookDao

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(this)

        // ✅ Use singleton database (correct way)
        val db = AppDatabase.getDatabase(this)
        bookDao = db.bookDao()

        setContent {
            NammaPustakaTheme {
                SplashNavigation()
            }
        }
    }
}

@Composable
fun AppStart() {

    var splash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(4000)
        splash = false
    }

    if (splash) {
        SplashScreen()
    } else {
        NavigationScreen()
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📚 Namma Pustaka", fontSize = 34.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Created by Sudeep", fontSize = 22.sp)
        }
    }
}

@Composable
fun NavigationScreen() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") { HomeScreen(navController) }

        composable("addbook") { AddBookScreen(navController) }

        composable("viewbook") { ViewBooksScreen(navController) }

        composable("search") { SearchBooksScreen(navController) }

        composable("login") { LoginScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("borrow") { BorrowScreen(navController) }
        composable("admin") { AdminScreen(navController) }
        composable("review") { ReviewScreen(navController) }
        composable("history") {
            HistoryScreen(navController)
        }

        composable("fine") {
            FineScreen(navController)
        }

        composable("leaderboard") {
            LeaderboardScreen(navController)
        }

        composable("qrscan") {
            QRScreen(navController)
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val scope = rememberCoroutineScope()   // ✅ IMPORTANT

    // ✅ LOAD DATA FROM ROOM (FIXED)
    LaunchedEffect(Unit) {
        scope.launch {

            val books = bookDao.getAllBooks()

            bookList.clear()
            authorList.clear()
            categoryList.clear()

            books.forEach {
                bookList.add(it.name)
                authorList.add(it.author)
                categoryList.add(it.category)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3C72),
                        Color(0xFF2A5298),
                        Color(0xFF6DD5FA)
                    )
                )
            )
            .padding(20.dp)
    ) {

        // 🎓 LOGIN BUTTON
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.align(Alignment.TopEnd),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "🎓 Student Login",
                color = Color.Black,   // ✅ FIXED
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "📚 Namma Pustaka",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Smart Library App",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.95f)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    PremiumButton("➕ Add Book") {
                        navController.navigate("addbook")
                    }

                    PremiumButton("📘 View Books") {
                        navController.navigate("viewbook")
                    }

                    PremiumButton("🔍 Search Books") {
                        navController.navigate("search")
                    }

                    PremiumButton("🏆 Reading Leaderboard") {
                        navController.navigate("leaderboard")
                    }

                    PremiumButton("📷 QR Scanner") {
                        navController.navigate("qrscan")
                    }

                    PremiumButton("🛡 Admin Dashboard") {
                        navController.navigate("admin")
                    }
                }
            }
        }
    }
}
@Composable
fun PremiumButton(text: String, onClick: () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E3C72)
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AddBookScreen(navController: androidx.navigation.NavHostController) {

    var bookName by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Select Category") }
    var expanded by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val categories = listOf(
        "Story",
        "Science",
        "History",
        "Coding"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⬅ Back", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "➕ Add Book",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00BFFF)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ Book Name
        OutlinedTextField(
            value = bookName,
            onValueChange = { bookName = it },
            label = { Text("Book Name", color = Color.Gray) },
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ✅ Author Name
        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text("Author Name", color = Color.Gray) },
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Select Category",
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box {

            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Category: $category", color = Color.Black)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                categories.forEach { item ->

                    DropdownMenuItem(
                        text = { Text(item, color = Color.Black) },
                        onClick = {
                            category = item   // ✅ proper selection
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val cleanBook = bookName.trim()
                val cleanAuthor = authorName.trim()
                val selectedCategory = category.trim()

                when {

                    cleanBook.isEmpty() || cleanAuthor.isEmpty() -> {
                        message = "Enter All Details ❌"
                    }

                    selectedCategory == "Select Category" -> {
                        message = "Please Select Category ⚠️"
                    }

                    else -> {

                        scope.launch {
                            bookDao.insertBook(
                                BookEntity(
                                    name = cleanBook,
                                    author = cleanAuthor,
                                    category = selectedCategory   // ✅ FIXED
                                )
                            )
                        }

                        message = "Book Added Successfully ✅"

                        bookName = ""
                        authorName = ""
                        category = "Select Category"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Book", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = message,
            fontSize = 20.sp,
            color = when {
                message.contains("Successfully") -> Color(0xFF4CAF50)
                message.contains("⚠️") -> Color(0xFFFF9800)
                else -> Color.Red
            }
        )
    }
}

@Composable
fun ViewBooksScreen(navController: androidx.navigation.NavHostController) {

    var selectedCategory by remember { mutableStateOf("All") }
    var showSummary by remember { mutableStateOf(false) }
    var summaryText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var books by remember { mutableStateOf(listOf<BookEntity>()) }

    LaunchedEffect(Unit) {
        books = bookDao.getAllBooks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📚 View Books",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf("All", "Story", "Science", "History", "Coding").forEach { cat ->

                Button(onClick = { selectedCategory = cat }) {
                    Text(cat)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (books.isEmpty()) {

            Text("No Books Added Yet", fontSize = 20.sp)

        } else {

            books.forEach { book ->

                if (selectedCategory == "All" || selectedCategory == book.category) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(15.dp)
                        ) {

                            Text(
                                text = "📘 ${book.name}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            // ✅ SAFE AUTHOR DISPLAY
                            Text(
                                text = "Author: ${book.author.ifEmpty { "Unknown" }}",
                                fontSize = 18.sp
                            )

                            Text(
                                text = "Category: ${book.category}",
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {

                                    summaryText =
                                        when (book.name.lowercase()) {

                                            "java" ->
                                                "☕ Java is used for Android apps."

                                            "python" ->
                                                "🐍 Python is used in AI & web."

                                            "kotlin" ->
                                                "📱 Kotlin is for Android development."

                                            else ->
                                                "📚 Useful educational book."
                                        }

                                    showSummary = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🤖 AI Summary")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ✅ FIXED DELETE (ONLY ONE BOOK)
                            Button(
                                onClick = {
                                    scope.launch {
                                        bookDao.deleteBookById(book.id)
                                        books = bookDao.getAllBooks()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🗑 Delete Book")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }

    if (showSummary) {

        AlertDialog(
            onDismissRequest = { showSummary = false },

            confirmButton = {
                Button(onClick = { showSummary = false }) {
                    Text("OK")
                }
            },

            title = { Text("🤖 GenAI Book Summary") },

            text = { Text(summaryText) }
        )
    }
}
@Composable
fun SearchBooksScreen(navController: androidx.navigation.NavHostController) {

    var searchText by remember { mutableStateOf("") }
    var searchType by remember { mutableStateOf("Book") }
    val context = LocalContext.current
    val view = LocalView.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "🔍 Search Books",
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = searchType == "Book",
                onClick = { searchType = "Book" }
            )
            Text("Book Name")

            Spacer(modifier = Modifier.width(20.dp))

            RadioButton(
                selected = searchType == "Author",
                onClick = { searchType = "Author" }
            )
            Text("Author Name")
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = {
                Text(
                    if (searchType == "Book")
                        "Enter Book Name"
                    else
                        "Enter Author Name"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (searchText.isNotEmpty()) {

            val resultIndexes =
                if (searchType == "Book") {

                    bookList.indices.filter { index ->
                        bookList[index].contains(
                            searchText.trim(),
                            ignoreCase = true
                        )
                    }

                } else {

                    authorList.indices.filter { index ->
                        authorList[index].contains(
                            searchText.trim(),
                            ignoreCase = true
                        )
                    }
                }

            if (resultIndexes.isEmpty()) {

                Text(
                    text = "No Books Found",
                    fontSize = 20.sp
                )

            } else {

                resultIndexes.forEach { index ->

                    Text(
                        text = "📘 ${bookList[index]}",
                        fontSize = 20.sp
                    )

                    Text(
                        text = "✍ ${authorList[index]}",
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }

        } else {

            Text(
                text = "Type to Search...",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun LoginScreen(navController: androidx.navigation.NavHostController) {

    var studentName by remember { mutableStateOf("") }
    var collegeId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val context = LocalContext.current
    val view = LocalView.current

    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val launcher =
        androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
        ) {
            imageUri = it
        }

    // ✅ LOAD DATA AFTER RESTART
    LaunchedEffect(Unit) {

        val prefs = context.getSharedPreferences("MyAppData", Context.MODE_PRIVATE)

        studentName = prefs.getString("student_name", "") ?: ""
        collegeId = prefs.getString("college_id", "") ?: ""
        phoneNumber = prefs.getString("phone", "") ?: ""
        gmail = prefs.getString("gmail", "") ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("👨‍🎓 Student Login ", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = studentName,
            onValueChange = { studentName = it },
            label = { Text("Student Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = collegeId,
            onValueChange = { collegeId = it },
            label = { Text("College ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = gmail,
            onValueChange = { gmail = it },
            label = { Text("Gmail Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                launcher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload ID Card Photo")
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (imageUri != null) {

            Text("✅ Photo Selected")

            Spacer(modifier = Modifier.height(10.dp))

            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(
                    android.R.drawable.ic_menu_gallery
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (studentName.isEmpty()) {
                    message = "Enter Student Name ❌"
                }
                else if (collegeId.isEmpty()) {
                    message = "Enter College ID ❌"
                }
                else if (phoneNumber.length != 10) {
                    message = "Enter Valid Phone Number ❌"
                }
                else if (!gmail.contains("@gmail.com")) {
                    message = "Enter Valid Gmail ❌"
                }
                else if (imageUri == null) {
                    message = "Upload ID Card Photo ❌"
                }
                else {

                    // ✅ SAVE DATA (IMPORTANT)
                    val prefs = context.getSharedPreferences("MyAppData", Context.MODE_PRIVATE)

                    prefs.edit()
                        .putString("student_name", studentName)
                        .putString("college_id", collegeId)
                        .putString("phone", phoneNumber)
                        .putString("gmail", gmail)
                        .apply()

                    savedStudentName = studentName
                    savedCollegeId = collegeId
                    savedPhone = phoneNumber
                    savedGmail = gmail

                    message = "Registered Successfully ✅"

                    navController.navigate("profile")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(message, fontSize = 20.sp)

        totalStudents = 1
    }
}

@Composable
fun ProfileScreen(navController: androidx.navigation.NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1F1C2C),
                        Color(0xFF928DAB)
                    )
                )
            )
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "⬅ Back",
                color = Color(0xFF1F1C2C),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "👤 Student Profile",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = "👨 Name: $savedStudentName",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🏫 College ID: $savedCollegeId",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "📞 Phone: $savedPhone",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "📧 Gmail: $savedGmail",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        PremiumProfileButton("📚 Borrow Book") {
            navController.navigate("borrow")
        }

        PremiumProfileButton("📜 Borrow History") {
            navController.navigate("history")
        }

        PremiumProfileButton("💰 Fine Details") {
            navController.navigate("fine")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📘 Borrowed Books",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (borrowedBooks.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Text(
                    text = "No Books Borrowed",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            }

        } else {

            borrowedBooks.forEachIndexed { index, book ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "📘 $book",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "⏰ ${dueDates[index]}",
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                borrowedBooks.removeAt(index)
                                dueDates.removeAt(index)
                                borrowedByList.removeAt(index)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1F1C2C)
                            )
                        ) {
                            Text(
                                text = "Return Book",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PremiumProfileButton(text: String, onClick: () -> Unit) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        )
    ) {
        Text(
            text = text,
            color = Color(0xFF1F1C2C),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
    }
}

@Composable
fun BorrowScreen(navController: NavHostController) {

    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "⬅ Back",
                color = Color(0xFF203A43),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📚 Borrow Books",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Book") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF2C5364),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf("All", "Story", "Science", "History", "Coding").forEach { cat ->

                Button(
                    onClick = { selectedCategory = cat },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (selectedCategory == cat)
                                Color.White
                            else
                                Color.White.copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = cat,
                        color = Color(0xFF203A43),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        bookList.forEachIndexed { index, book ->

            val category = categoryList[index]

            val matchesSearch =
                book.contains(searchText, ignoreCase = true)

            val matchesCategory =
                selectedCategory == "All" ||
                        selectedCategory == category

            if (matchesSearch && matchesCategory) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {

                        Text(
                            text = "📘 $book",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Category: $category",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {

                                if (!borrowedBooks.contains(book)) {

                                    borrowedBooks.add(book)
                                    dueDates.add("Due in 7 Days")
                                    borrowedByList.add(savedStudentName)

                                    showNotification(
                                        context,
                                        "📚 Return Reminder",
                                        "Please return $book tomorrow!"
                                    )

                                    Toast.makeText(
                                        context,
                                        "✅ Book Borrowed Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {

                                    Toast.makeText(
                                        context,
                                        "⚠️ Book Already Borrowed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF203A43)
                            )
                        ) {
                            Text(
                                text = "Borrow Now",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = {
                                generatePdf(
                                    context,
                                    "Borrow_Report",
                                    """NammaPustaka Borrow ReceiptStudent: $ savedStudentNameBook: ${bookList.joinToString()}Status: Borrowed  """.trimIndent()
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📄 Download Borrow PDF")
                        }
                    }
                    Button(
                        onClick = {

                            val activity = context as Activity
                            val view = activity.window.decorView.rootView

                            captureAndSavePdf(
                                view,
                                context,
                                "MyPDF"
                            )

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📄 Download PDF")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("review") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "⭐ Rate / Review Book",
                color = Color(0xFF203A43),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("qrscan") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "📷 QR Borrow / Return",
                color = Color(0xFF203A43),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ReviewScreen(navController: androidx.navigation.NavHostController) {

    var selectedBook by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("⭐ Book Review", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = selectedBook,
            onValueChange = { selectedBook = it },
            label = { Text("Book Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating (1 to 5)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = review,
            onValueChange = { review = it },
            label = { Text("Write Review") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                ratingList.add("📘 $selectedBook - ⭐ $rating")
                reviewList.add(review)
                message = "Review Submitted ✅"
                selectedBook = ""
                rating = ""
                review = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Review")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(message, fontSize = 20.sp)
    }
}

@Composable
fun AdminScreen(navController: NavHostController) {

    var pin by remember { mutableStateOf("") }
    var loginSuccess by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val view = LocalView.current

    // ✅ LOAD DATA AFTER RESTART
    LaunchedEffect(Unit) {

        val prefs = context.getSharedPreferences("MyAppData", Context.MODE_PRIVATE)

        totalStudents = prefs.getInt("total_students", 0)
        loginSuccess = prefs.getBoolean("admin_logged_in", false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D0D),
                        Color(0xFF121212),
                        Color(0xFF1A1A2E)
                    )
                )
            )
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00BCD4)
            )
        ) {
            Text("⬅ Back", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "👨‍🏫 Admin Dashboard",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            ),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {

            Column(modifier = Modifier.padding(18.dp)) {

                Text(
                    text = "📚 Total Books: ${bookList.size}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "👨‍🎓 Registered Students: $totalStudents",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "📖 Borrowed Books: ${borrowedBooks.size}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // 📄 DOWNLOAD REPORT
        Button(
            onClick = {

                val prefs = context.getSharedPreferences("MyAppData", Context.MODE_PRIVATE)

                // ✅ SAVE DATA BEFORE EXPORT
                prefs.edit()
                    .putInt("total_students", totalStudents)
                    .putBoolean("admin_logged_in", true)
                    .apply()

                generatePdf(
                    context,
                    "Admin_Report",
                    """
Admin Report

Total Books: ${bookList.size}
Borrowed Books: ${borrowedBooks.size}
Students: $totalStudents
Created by Sudeep G
                    """.trimIndent()
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9C27B0)
            )
        ) {
            Text("📄 Download Report PDF", color = Color.White)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 🚪 LOGOUT BUTTON (FIXED)
        Button(
            onClick = {

                val prefs = context.getSharedPreferences("MyAppData", Context.MODE_PRIVATE)

                prefs.edit()
                    .putBoolean("admin_logged_in", false)
                    .apply()

                loginSuccess = false
                pin = ""

                Toast.makeText(
                    context,
                    "Logged Out ✅",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            Text("🚪 Logout", color = Color.White)
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 📸 SCREENSHOT PDF
        Button(
            onClick = {

                val activity = context as Activity
                val rootView = activity.window.decorView.rootView

                captureAndSavePdf(
                    rootView,
                    context,
                    "AdminScreenPDF"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📄 Download Screen PDF")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun GlassCardText(textValue: String) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Text(
            text = textValue,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
fun HistoryScreen(navController: androidx.navigation.NavHostController) {
    val context = LocalContext.current
    val view = LocalView.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("📜 Borrow History", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(20.dp))

        if (borrowedBooks.isEmpty()) {

            Text("No Borrow History")

        } else {

            borrowedBooks.forEachIndexed { index, book ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text("📘 $book", fontSize = 20.sp)
                        Text("⏰ ${dueDates[index]}", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun FineScreen(navController: NavHostController) {

    val context = LocalContext.current
    val view = LocalView.current

    var lateDays by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var fineAmount by remember { mutableStateOf("0") }
    var paymentStatus by remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { res ->

            if (res.resultCode == Activity.RESULT_OK) {

                paymentStatus = "Payment Successful ✅"
                fineAmount = "0"
                result = "Total Fine: ₹0"
                lateDays = ""

            } else {

                paymentStatus = "Payment Failed ❌"
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "⬅ Back",
                color = Color(0xFF203A43),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "💰 Fine Payment",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = lateDays,
                    onValueChange = { lateDays = it },
                    label = { Text("Enter Late Days") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {

                        val days = lateDays.toIntOrNull() ?: 0
                        val fine = days * 10

                        fineAmount = fine.toString()
                        result = "₹$fine"
                        paymentStatus = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF203A43)
                    )
                ) {
                    Text(
                        text = "Calculate Fine",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Total Fine",
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                Text(
                    text = result.ifEmpty { "₹0" },
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF203A43)
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {

                        val uri = Uri.parse(
                            "upi://pay?pa=9108727807@ybl&pn=NammaPustaka&am=$fineAmount&cu=INR"
                        )

                        val intent = Intent(Intent.ACTION_VIEW, uri)

                        try {
                            launcher.launch(intent)
                        } catch (e: Exception) {
                            paymentStatus = "No UPI App Found ❌"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A)
                    )
                ) {
                    Text(
                        text = "📲 Pay Now",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        if (paymentStatus.isNotEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {

                Text(
                    text = paymentStatus,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (paymentStatus.contains("Successful"))
                        Color(0xFF16A34A)
                    else
                        Color.Red
                )
            }
            Button(
                onClick = {
                    generatePdf(
                        context,
                        "Fine_Receipt",
                        """
Fine Payment Successful

Student: $savedStudentName
Fine Amount: ₹50
Status: Paid
            """.trimIndent()
                    )
                }
            ) {
                Text("📄 Download Fine PDF")
            }
            Button(
                onClick = {

                    val activity = context as Activity
                    val view = activity.window.decorView.rootView

                    captureAndSavePdf(
                        view,
                        context,
                        "MyPDF"
                    )

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📄 Download PDF")
            }
        }
    }
}

@Composable
fun LeaderboardScreen(navController: NavHostController) {

    val context = LocalContext.current

    var leaderboardList by remember { mutableStateOf(listOf<Pair<String, Int>>()) }

    // ✅ LOAD SAVED DATA
    LaunchedEffect(Unit) {

        val prefs = context.getSharedPreferences("MyAppData", Context.MODE_PRIVATE)
        val json = prefs.getString("leaderboard_data", null)

        if (json != null) {
            val type = object : TypeToken<List<Pair<String, Int>>>() {}.type
            leaderboardList = Gson().fromJson(json, type)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "🏆 Reading Leaderboard",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (leaderboardList.isEmpty()) {

            Text("No Borrow Records Yet", fontSize = 20.sp)

        } else {

            leaderboardList.forEachIndexed { index, item ->

                val medal =
                    when (index) {
                        0 -> "🥇"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> "🏅"
                    }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "$medal ${item.first}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${item.second} Books",
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 📄 DOWNLOAD PDF
        Button(
            onClick = {

                val activity = context as Activity
                val rootView = activity.window.decorView.rootView

                captureAndSavePdf(
                    rootView,
                    context,
                    "LeaderboardPDF"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📄 Download PDF")
        }
    }
}

@Composable
fun QRScreen(navController: NavHostController) {

    val context = LocalContext.current
    var result by remember { mutableStateOf("") }

    var scanType by remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { res ->

            val scanResult =
                IntentIntegrator.parseActivityResult(
                    res.resultCode,
                    res.data
                )

            if (scanResult.contents != null) {

                val code = scanResult.contents.trim()

                result = when (code) {

                    "BORROW_B101" ->
                        "📘 Java Book Borrowed Successfully ✅"

                    "BORROW_B102" ->
                        "📘 Python Book Borrowed Successfully ✅"

                    "BORROW_B103" ->
                        "📘 History Book Borrowed Successfully ✅"

                    "RETURN_B101" ->
                        "📘 Java Book Returned Successfully 🔄"

                    "RETURN_B102" ->
                        "📘 Python Book Returned Successfully 🔄"

                    "RETURN_B103" ->
                        "📘 History Book Returned Successfully 🔄"

                    else ->
                        "❌ Invalid QR Code"
                }

            } else {
                result = "Scan Cancelled ❌"
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141E30),
                        Color(0xFF243B55)
                    )
                )
            )
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "⬅ Back",
                color = Color(0xFF243B55),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "📷 QR Library Scanner",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.12f)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "◉",
                    fontSize = 70.sp,
                    color = Color.White
                )

                Text(
                    text = "Scan Book QR Code",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {

                scanType = "borrow"

                val integrator =
                    IntentIntegrator(context as Activity)

                integrator.setDesiredBarcodeFormats(
                    IntentIntegrator.QR_CODE
                )
                integrator.setPrompt("Scan Borrow QR")
                integrator.setBeepEnabled(true)
                integrator.setBarcodeImageEnabled(true)
                integrator.setOrientationLocked(false)
                integrator.setCameraId(0)

                launcher.launch(integrator.createScanIntent())

            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF16A34A)
            )
        ) {
            Text(
                text = "📥 Borrow Scan",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {

                scanType = "return"

                val integrator =
                    IntentIntegrator(context as Activity)

                integrator.setDesiredBarcodeFormats(
                    IntentIntegrator.QR_CODE
                )
                integrator.setPrompt("Scan Return QR")
                integrator.setBeepEnabled(true)
                integrator.setBarcodeImageEnabled(true)
                integrator.setOrientationLocked(false)
                integrator.setCameraId(0)

                launcher.launch(integrator.createScanIntent())

            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            )
        ) {
            Text(
                text = "📤 Return Scan",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        if (result.isNotEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {

                Text(
                    text = result,
                    modifier = Modifier.padding(18.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF243B55)
                )
            }
        }
    }
}

@Composable
fun SplashNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("home") {
            NavigationScreen()
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {

    val infiniteTransition =
        rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    LaunchedEffect(Unit) {
        delay(3000)

        navController.navigate("home") {
            popUpTo("splash") {
                inclusive = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale)
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Text(
                text = "NAMMA PUSTAKA",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "Smart AI Library App",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            CircularProgressIndicator(
                color = Color.White
            )
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Created & Maintained by Sudeep G",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}


fun createNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val channel = NotificationChannel(
            "library_channel",
            "Library Reminder",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.createNotificationChannel(channel)
    }
}


fun showNotification(
    context: Context,
    title: String,
    message: String
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        if (
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val builder =
        NotificationCompat.Builder(
            context,
            "library_channel"
        )
            .setSmallIcon(
                android.R.drawable.ic_dialog_info
            )
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(
                NotificationCompat.PRIORITY_HIGH
            )
            .setAutoCancel(true)

    NotificationManagerCompat
        .from(context)
        .notify(1, builder.build())
}


fun generatePdf(context: Context, fileName: String, content: String) {

    try {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        paint.textSize = 14f

        var y = 40

        content.split("\n").forEach {
            canvas.drawText(it, 10f, y.toFloat(), paint)
            y += 25
        }

        pdfDocument.finishPage(page)

        // ✅ SAFE STORAGE (no crash)
        val file = File(
            context.getExternalFilesDir(null),
            "$fileName.pdf"
        )

        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        Toast.makeText(
            context,
            "PDF Saved Successfully ✅",
            Toast.LENGTH_LONG
        ).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
fun captureAndSavePdf(view: View, context: Context, fileName: String) {

    try {

        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(
            bitmap.width,
            bitmap.height,
            1
        ).create()

        val page = pdfDocument.startPage(pageInfo)
        val pdfCanvas = page.canvas

        pdfCanvas.drawBitmap(bitmap, 0f, 0f, null)

        pdfDocument.finishPage(page)

        val file = File(
            context.getExternalFilesDir(null),
            "$fileName.pdf"
        )

        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        Toast.makeText(
            context,
            "PDF Saved ✅",
            Toast.LENGTH_LONG
        ).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}