import 'package:encrypted_shared_preferences/encrypted_shared_preferences.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'menu.dart'; // gives us BrowseCategoriesPage

void main() {
  runApp(const MyApp());
}

// ---------------------------------------------------------------------------
// TRANSLATIONS
// Every piece of text on screen is looked up here by a key.
// ---------------------------------------------------------------------------
const Map<String, Map<String, String>> translations = {
  'en': {
    'title': 'Login',
    'loginName': 'Login name',
    'password': 'Password',
    'loginButton': 'Login',
    'loginButton1': 'Open menu',
    'switchLanguage': 'فارسی', // shown on the button while in English
    'saveDialogTitle': 'Save login?',
    'saveDialogContent':
        'Would you like to save your login name and password for the next time you run the application?',
    'yes': 'Yes',
    'no': 'No',
    'loadedSnackBar': 'The previous login name and password have been loaded.',
  },
  'fa': {
    'title': 'ورود',
    'loginName': 'نام کاربری',
    'password': 'رمز عبور',
    'loginButton': 'ورود',
    'loginButton1': 'باز کردن منو',
    'switchLanguage': 'English', // shown on the button while in Persian
    'saveDialogTitle': 'ذخیره اطلاعات ورود؟',
    'saveDialogContent':
        'آیا می‌خواهید نام کاربری و رمز عبور شما برای دفعه بعد که برنامه را اجرا می‌کنید ذخیره شود؟',
    'yes': 'بله',
    'no': 'خیر',
    'loadedSnackBar': 'نام کاربری و رمز عبور قبلی بارگذاری شد.',
  },
};

// Keys used to store the strings in EncryptedSharedPreferences.
const String loginNameKey = 'loginName';
const String passwordKey = 'password';

// ---------------------------------------------------------------------------
// APP
// ---------------------------------------------------------------------------
class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Locale _locale = const Locale('en');

  void _toggleLanguage() {
    setState(() {
      _locale = _locale.languageCode == 'en'
          ? const Locale('fa')
          : const Locale('en');
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      ),
      locale: _locale,
      supportedLocales: const [Locale('en'), Locale('fa')],
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      home: LoginPage(
        languageCode: _locale.languageCode,
        onToggleLanguage: _toggleLanguage,
      ),
    );
  }
}

// ---------------------------------------------------------------------------
// LOGIN PAGE
// ---------------------------------------------------------------------------
class LoginPage extends StatefulWidget {
  const LoginPage({
    super.key,
    required this.languageCode,
    required this.onToggleLanguage,
  });

  final String languageCode;
  final VoidCallback onToggleLanguage;

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController _loginNameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final EncryptedSharedPreferences _prefs = EncryptedSharedPreferences();

  // The image source starts as the question mark in the images folder.
  var imageSource = "images/question-mark.png";

  // Returns the text for [key] in the current language.
  String tr(String key) => translations[widget.languageCode]![key]!;

  @override
  void initState() {
    super.initState();
    _loadSavedLogin();
  }

  @override
  void dispose() {
    _loginNameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  // On startup: if a login name and password were saved, put them in the
  // TextFields and show a SnackBar. getString returns "" when nothing is saved.
  Future<void> _loadSavedLogin() async {
    String loginName = await _prefs.getString(loginNameKey);
    String password = await _prefs.getString(passwordKey);
    if (!mounted) return;

    if (loginName.isNotEmpty && password.isNotEmpty) {
      _loginNameController.text = loginName;
      _passwordController.text = password;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(tr('loadedSnackBar'))),
      );
    }
  }

  // Button 1 "Login": checks the password, changes the image, then asks
  // whether to save the login name and password.
  void _onLoginPressed() {
    String password = _passwordController.text;

    setState(() {
      if (password == "ASDF") {
        imageSource = "images/idea.png";
      } else {
        imageSource = "images/stop.png";
      }
    });

    _showSaveDialog();
  }

  // "Yes" saves both strings; "No" clears anything saved before.
  void _showSaveDialog() {
    showDialog<void>(
      context: context,
      barrierDismissible: false, // the user must choose Yes or No
      builder: (dialogContext) => AlertDialog(
        title: Text(tr('saveDialogTitle')),
        content: Text(tr('saveDialogContent')),
        actions: [
          TextButton(
            onPressed: () {
              _prefs.clear();
              Navigator.pop(dialogContext);
            },
            child: Text(tr('no')),
          ),
          TextButton(
            onPressed: () {
              _prefs.setString(loginNameKey, _loginNameController.text);
              _prefs.setString(passwordKey, _passwordController.text);
              Navigator.pop(dialogContext);
            },
            child: Text(tr('yes')),
          ),
        ],
      ),
    );
  }

  // Button 2 "Open menu": opens menu.dart with no password check.
  void _onLoginPressed1() {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => const BrowseCategoriesPage(),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(tr('title')),
        actions: [
          TextButton.icon(
            onPressed: widget.onToggleLanguage,
            icon: const Icon(Icons.language),
            label: Text(tr('switchLanguage')),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            TextField(
              controller: _loginNameController,
              decoration: InputDecoration(
                labelText: tr('loginName'),
                border: const OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),

            TextField(
              controller: _passwordController,
              obscureText: true,
              decoration: InputDecoration(
                labelText: tr('password'),
                border: const OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 24),

            // Button 1: Login (password check + image + save dialog)
            ElevatedButton(
              onPressed: _onLoginPressed,
              child: Text(tr('loginButton')),
            ),
            const SizedBox(height: 16),

            // Button 2: Open menu (no authentication)
            ElevatedButton(
              onPressed: _onLoginPressed1,
              child: Text(tr('loginButton1')),
            ),
            const SizedBox(height: 24),

            Image.asset(
              imageSource,
              width: 300,
              height: 300,
            ),
          ],
        ),
      ),
    );
  }
}
