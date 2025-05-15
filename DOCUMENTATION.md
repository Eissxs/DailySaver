# DailySaver - Android App Documentation (Dark + iOS-Inspired Theme)

## Project Overview
DailySaver is a modern Android application designed to help users track their daily savings with an elegant, iOS-inspired dark theme interface. The app focuses on providing a seamless user experience with smooth animations and intuitive interactions.

## Technical Stack
- **Language**: Java
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Database**: Room Persistence Library
- **Build System**: Gradle 8.2.2
- **Dependencies**: MPAndroidChart for visualizations

## Theme Specifications
### Colors
- Background: #121212 (Deep Charcoal)
- Card Background: #1F1F1F
- Primary Accent: #00C897 (Teal)
- Secondary: #FFD369 (Gold)
- Text Primary: #FFFFFF
- Text Secondary: #AAAAAA

### Typography
- Title: Roboto 24sp SemiBold
- Body: Roboto 16sp Regular
- Caption: Roboto 13sp Light

### App Icons & Branding
- Custom app icon with squircle adaptive icon support
- Icon padding: 15% inset for optimal display
- Monochrome icon support for Android 13+

### Splash Screen
- Custom squircle-shaped logo container (40dp radius)
- Primary accent color background
- 150dp x 150dp logo size
- 28sp app name text size
- Animated loading indicator (42dp)
- Fade-in animations for all elements

## Project Structure
```
app/
├── java/com/Eissxs/dailysaver/
│   ├── activities/
│   │   ├── SplashActivity.java
│   │   ├── HomeActivity.java
│   │   ├── AddEntryActivity.java
│   │   ├── HistoryActivity.java
│   │   ├── GoalTrackerActivity.java
│   │   └── SettingsActivity.java
│   ├── adapters/
│   │   └── SavingEntryAdapter.java
│   ├── database/
│   │   ├── AppDatabase.java
│   │   ├── DateConverter.java
│   │   ├── SavingDao.java
│   │   ├── SavingEntry.java
│   │   └── SavingGoal.java
│   └── utils/
│       └── NotificationHelper.java
└── res/
    ├── layout/
    │   ├── activity_splash.xml
    │   ├── activity_home.xml
    │   ├── activity_add_entry.xml
    │   ├── activity_history.xml
    │   ├── activity_goal_tracker.xml
    │   └── item_saving_entry.xml
    ├── anim/
    │   ├── slide_up.xml
    │   ├── slide_down.xml
    │   └── fade_in.xml
    └── drawable/
        ├── ic_money.xml
        ├── ic_note.xml
        └── ic_empty_list.xml
```

## Features
1. **Savings Entry Management**
   - Add new savings with amount and note
   - View history in chronological order
   - Swipe-to-delete entries
   - Edit existing entries

2. **Goal Tracking**
   - Set savings goals
   - Visual progress tracking
   - Milestone celebrations
   - Progress ring visualization

3. **History & Analytics**
   - Chronological list of all entries
   - Monthly/yearly summaries
   - Trend visualization using MPAndroidChart
   - Swipe gestures for entry management

4. **UI/UX Features**
   - Dark theme optimized for OLED displays
   - iOS-style animations and transitions
   - Card-based layout design
   - Intuitive navigation

## Database Schema
### SavingEntry
```java
@Entity(tableName = "saving_entries")
public class SavingEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public float amount;
    public String note;
    public Date date;
}
```

### SavingGoal
```java
@Entity(tableName = "saving_goals")
public class SavingGoal {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public float goalAmount;
}
```

## Animation Specifications
- Modal Transitions: 300ms ease-in-out
- Card Animations: 200ms ease-out
- Progress Updates: 500ms with interpolation
- Swipe Gestures: Spring animation

## Build Instructions
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator (min SDK 24)

## Future Enhancements
- [ ] Dark/Light theme toggle
- [ ] Multiple savings goals
- [ ] Export data functionality
- [ ] Cloud backup integration
- [ ] Widget support