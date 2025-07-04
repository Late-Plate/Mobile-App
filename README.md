# 🍽️ Late Plate – Android Mobile App

**Late Plate** is a smart cooking assistant powered by AI. This mobile app brings together personalized recipe generation, smart recommendations, ingredient recognition, and real-time cooking guidance — all in one intuitive Android experience.

## 📱 Features

- 🔍 **AI-Powered Recipe Generation**  
  Generate custom recipes based on ingredients you have at home using fine-tuned GPT-2 and LLaMA models.

- ⭐ **Personalized Recommendations**  
  Get smart meal suggestions based on your preferences, history, and popular trends using a hybrid recommendation system.

- 🥦 **Offline Ingredient Detection**  
  Identify ingredients from camera images using a YOLOv11s model running offline via TensorFlow Lite.

- 🧑‍🍳 **Cooking Assistant**  
  Step-by-step instructions to guide you through the cooking process.

- 🛒 **Grocery List & Inventory Tracker**  
  Easily track your pantry items and build dynamic grocery lists.

- 🔐 **User Authentication**  
  Secure sign-up and login via Firebase Authentication.

---

<details>
  <summary>📸 Screenshots (click to expand)</summary>

![Picture6](https://github.com/user-attachments/assets/2407a2a8-cd32-4c7d-af4f-e2bce1db8af6)
![Picture5](https://github.com/user-attachments/assets/1dd9d20b-e118-4fad-80dc-0de3b91320f2)
![Picture3](https://github.com/user-attachments/assets/628cba56-c677-4553-9561-488f1dd71034)
![Picture4](https://github.com/user-attachments/assets/d225f34f-917b-4239-ae21-a5e0b956702b)
![Picture7](https://github.com/user-attachments/assets/e0b33485-1c24-4690-bcc7-e96098e0a407)
![Picture8](https://github.com/user-attachments/assets/44f12a96-24ec-4ade-b020-e70696158040)
![Picture9](https://github.com/user-attachments/assets/da258ee8-54cf-48e7-b3f8-db15c49e2b94)
![Picture10](https://github.com/user-attachments/assets/8158a813-edf4-423e-8604-1a624fba0ae0)
![Picture11](https://github.com/user-attachments/assets/3349d444-6fac-47f7-b4e7-4261ff0f2350)
![Picture12](https://github.com/user-attachments/assets/6459b25b-55dd-4c73-a95f-2361c1eab847)
![Picture13](https://github.com/user-attachments/assets/4e4b683a-4402-4d9d-8b40-3032cade1c49)

</details>



---

## 🎥 Demo Video
https://github.com/user-attachments/assets/516059cd-2e5f-48d6-9f64-2991ca8210d7

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **IDE:** Android Studio
- **State Management:** ViewModel
- **Backend Services:** Flask + Spring Boot APIs
- **Authentication:** Firebase Auth
- **Cloud Storage:** Firebase Storage
- **ML Integration:** TensorFlow Lite (on-device YOLOv11s)

---

## 📦 Installation

1. Clone the repository:

```bash
git clone https://github.com/YOUR_ORG/mobile-app.git
```
2. Open in Android Studio.
3. Make sure to add your google-services.json file to the /app directory.
4. Build and run the app on an emulator or device (minimum SDK 21).

---

##  📲 Permissions
The app requires the following permissions:

- CAMERA – for real-time ingredient recognition
- INTERNET – to communicate with backend APIs

---

## 📄 License

Each model may be released under a separate license. Please refer to the `README.md` and `LICENSE` file in each branch for more details.

---

## 📬 Contact

For questions or contributions, feel free to open an issue or contact the team via the main [Late Plate organization page](https://github.com/YOUR_ORG).
