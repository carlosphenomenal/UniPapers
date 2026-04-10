package com.unipapers.unipapers_frontend.feature.profile.presentation.components
@Composable
fun ProfileStatsRow(user: User) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = user.fullName,
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0 split: 0x0D1B4B) // Navy Blue
        )
        Text(text = user.programme, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // Status Badge
        Surface(
            color = if (user.hasUnlockedAccess) Color(0xFF4CAF50) else Color(0xFFF5A623), // Amber if locked
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = if (user.hasUnlockedAccess) "Full Access Unlocked" else "${user.freeViewsRemaining} Previews Left",
                color = Color.White
            )
        }
    }
}