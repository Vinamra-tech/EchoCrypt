import streamlit as st
from utils import render_sidebar  # Uncomment if sidebar is needed
def render_sidebar():
    st.sidebar.page_link("pages/1_home.py", label="🏠 Home")
    st.sidebar.page_link("pages/3_Login.py", label="🔐 Login")  # <- Does this exist??
    st.sidebar.page_link("pages/4_about.py", label="📘 About")
    st.sidebar.page_link("pages/5_contact.py", label="📞 Contact")
from utils import render_sidebar
render_sidebar()
# Custom CSS for styling

st.markdown("""
    <style>
    .stApp {
        background-color: #1A1A2E;
        margin-top: 0px;
        padding-top: 0px;
    }

    h1.animated-title {
        margin-top: 0px;
        padding-top: 0px;
        animation: fadeInDown 2s ease-in-out;
    }

    @keyframes fadeInDown {
        0% { opacity: 0; transform: translateY(-50px); }
        100% { opacity: 1; transform: translateY(0); }
    }

    .about-box {
        background-color: #16213E;
        padding: 30px;
        border-radius: 15px;
        color: #EAEAEA;
        font-size: 18px;
        line-height: 1.6;
        max-width: 800px;
        margin: auto;
        text-align: justify;
    }
    </style>
""", unsafe_allow_html=True)

# Header
st.markdown("<h1 class='animated-title' style='font-family: Verdana, sans-serif; font-size: 60px; text-align: center; color: #F72585;'>ABOUT SECTION</h1>", unsafe_allow_html=True)
st.markdown("Built by students for students — with privacy at the core. EchoCrypt started as a dorm room idea and now it's a mission to make encryption accessible for everyone.")
# Optional sidebar
# render_sidebar()

# About content
st.markdown("""
<div class="about-box">
    Welcome to <strong>EchoCrypt</strong>, your secure encryption and decryption solution!<br><br>
    EchoCrypt is designed to make securing your sensitive files simple, elegant, and reliable. With just a few clicks, you can lock your data with cutting-edge encryption — no technical experience required.<br><br>
    Whether you're protecting personal notes, audio files, or even sharing secrets via QR codes, EchoCrypt ensures everything stays 🔐 private and tamper-proof. <br><br>
    Ready to encrypt like a pro and decrypt with confidence? Dive in and take control of your data!
</div>
""", unsafe_allow_html=True)

st.markdown("""
<h3 style='color: #E94560; text-align:center;'>✨ How It Work</h3>
<div style='display: flex; justify-content: center; gap: 20px; flex-wrap: wrap;'>
    <div style='background-color:#16213E; padding:20px; border-radius:12px; width: 250px;'>
        <h4 style='color:white;'>🔐 File Encryption
</h4>
        <p style='color: grey;'>With EchoCrypt, simply upload your files and our custom encryption logic will lock them securely. The encryption is robust, ensuring your data stays safe from unauthorized access.
</p>
    </div>
    <div style='background-color:#16213E; padding:20px; border-radius:12px; width: 250px;'>
        <h4 style='color:white;'>Randomly Generated Passwords
</h4>
        <p style='color: grey;'>Unlike other systems that require manual password creation, EchoCrypt generates a random password for each file encryption. This makes it easier for you to keep track of passwords and ensures no two passwords are alike.
.</p>
    </div>
    <div style='background-color:#16213E; padding:20px; border-radius:12px; width: 250px;'>
        <h4 style='color:white;'>🧠 Password-Driven Decryption
</h4>
        <p style='color: grey;'>When its time to retrieve your files, youll be prompted to enter the randomly generated password to unlock them. Its a straightforward process with no scanning involved.
</p>
    </div>
    <div style='background-color:#16213E; padding:20px; border-radius:12px; width: 250px;'>
        <h4 style='color:white;'>No Algorithms 
</h4>
        <p style='color: grey;'>We’ve moved beyond traditional encryption algorithms like AES or RSA. Instead, EchoCrypt relies on unique custom logic that ensures your data stays safe while being more accessible and easier to use.

</p>
    </div>
</div>
""", unsafe_allow_html=True)
import streamlit as st

# CSS Styling for Table
st.markdown("""
    <style>
    .custom-table {
        width: 90%;
        margin: 30px auto;
        border-collapse: collapse;
        font-size: 16px;
        background-color: #16213E;
        color: #EAEAEA;
        border-radius: 12px;
        overflow: hidden;
    }

    .custom-table th, .custom-table td {
        padding: 15px 20px;
        text-align: left;
        border-bottom: 1px solid #2C2C54;
    }

    .custom-table th {
        background-color: #0F3460;
        color: #F72585;
        font-size: 18px;
    }

    .custom-table tr:hover {
        background-color: #1A1A2E;
    }

    .custom-table caption {
        font-size: 24px;
        font-weight: bold;
        color: #F72585;
        margin-bottom: 15px;
    }
    </style>
""", unsafe_allow_html=True)

# HTML Table Content
st.markdown("""
    <table class="custom-table">
        <caption>🔍 EchoCrypt Highlights</caption>
        <thead>
            <tr>
                <th>Feature</th>
                <th>Description</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>File Encryption</td>
                <td>Encrypts any type of file with custom logic</td>
                <td>✅ Completed</td>
            </tr>
            <tr>
                <td>QR Code Support</td>
                <td>Converts encrypted data into scannable QR</td>
                <td>✅ Completed</td>
            </tr>
            <tr>
                <td>Password Generator</td>
                <td>Creates a random password for each file</td>
                <td>✅ Completed</td>
            </tr>
            <tr>
                <td>Password Attempts Limit</td>
                <td>Triggers reset after 3 wrong attempts</td>
                <td>🕐 Coming Soon</td>
            </tr>
            <tr>
                <td>Audio Encryption</td>
                <td>Encrypt and decrypt voice messages</td>
                <td>🛠️ Building</td>
            </tr>
        </tbody>
    </table>
""", unsafe_allow_html=True)

st.markdown("<h2 style='color: #F72585; text-align: center;'>❓ Frequently Asked Questions</h2>", unsafe_allow_html=True)

with st.expander("🔐 What is EchoCrypt?"):
    st.write("EchoCrypt is a secure file encryption & decryption app that uses custom logic instead of traditional algorithms like AES or RSA.")

with st.expander("🔑 How are passwords generated?"):
    st.write("Each time you encrypt a file, a new random password is generated automatically. You don't need to create one manually!")

with st.expander("📁 What types of files can I encrypt?"):
    st.write("Currently, EchoCrypt supports text. We're working on expanding support to more formats soon!")

with st.expander("🧠 What is 'custom logic' encryption?"):
    st.write("Instead of using standard algorithms, EchoCrypt uses a unique pattern-based encryption process designed by our team. This keeps it simple, fast, and secure.")

with st.expander("📱 Will there be a mobile version?"):
    st.write("Yes! It's in the roadmap. We're focusing on web stability first, but a mobile-friendly version is coming 🔜.")
st.markdown("""
<hr style="border:1px solid #444"/>
<div style='text-align: center; color: grey; font-size: 14px;'>
    © 2025 EchoCrypt. Made with 💖 by Us.
</div>
""", unsafe_allow_html=True)
