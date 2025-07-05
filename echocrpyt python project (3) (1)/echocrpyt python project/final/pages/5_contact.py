import streamlit as st
from utils import render_sidebar

# Render Sidebar
render_sidebar()

# Custom CSS for styling
st.markdown("""
    <style>
    .stApp {
        background-color: #1A1A2E;
        margin-top: 0;
        padding-top: 0;
    }

    h1.animated-title {
        margin-top: 0px;
        padding-top: 0px;
        animation: slideInDown 1.5s ease-in-out;
    }

    @keyframes slideInDown {
        0% { opacity: 0; transform: translateY(-60px); }
        100% { opacity: 1; transform: translateY(0); }
    }

    .contact-container {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 30px;
        margin-top: 40px;
    }

    .contact-box {
        background-color: #16213E;
        padding: 25px;
        border-radius: 15px;
        width: 300px;
        color: #EAEAEA;
        box-shadow: 0 0 10px rgba(255,255,255,0.1);
        transition: transform 0.3s ease;
    }

    .contact-box:hover {
        transform: translateY(-10px);
    }

    .contact-title {
        font-size: 20px;
        font-weight: bold;
        color: #F72585;
        margin-bottom: 10px;
    }

    .contact-info {
        color: #B2B2B2;
        font-size: 16px;
    }

    hr {
        border: 1px solid #444;
        margin: 30px 0;
    }
    </style>
""", unsafe_allow_html=True)

# Header Title
st.markdown("<h1 class='animated-title' style='font-family: Verdana, sans-serif; font-size: 55px; text-align: center; color: #F72585;'>CONTACT US</h1>", unsafe_allow_html=True)

# Contact Section
st.markdown("""
<div class="contact-container">
    <div class="contact-box">
        <div class="contact-title">📧 Email:</div>
        <div class="contact-info">team.echocrpt.gsv@gmail.com</div>
    </div>
    <div class="contact-box">
        <div class="contact-title">📞 Phone</div>
        <div class="contact-info">+91 93504 20553</div>
    </div>
    <div class="contact-box">
        <div class="contact-title">📍 Address</div>
        <div class="contact-info">Vadodara, India<br/>560001</div>
    </div>
    <div class="contact-box">
        <div class="contact-title">💬 Chat Support</div>
        <div class="contact-info">Live chat coming soon on web & mobile!</div>
    </div>
</div>
""", unsafe_allow_html=True)

# Footer
st.markdown("""
<hr />
<div style='text-align: center; color: grey; font-size: 14px;'>
    Got questions or feedback? We’d love to hear from you 💌<br>
    © 2025 EchoCrypt. Crafted with 🔐 by Students.
</div>
""", unsafe_allow_html=True)
