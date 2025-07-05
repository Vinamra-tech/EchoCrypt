import streamlit as st
import time
from utils import render_sidebar

st.set_page_config(page_title="EchoCrypt", layout="centered")

# Sidebar
render_sidebar()

# 🎨 Custom CSS for animations
st.markdown("""
<style>
.animated-title {
    animation: fadeInDown 2s ease-in-out;
}
@keyframes fadeInDown {
  0% { opacity: 0; transform: translateY(-50px); }
  100% { opacity: 1; transform: translateY(0); }
}

@keyframes slideInWordByWord {
    0% {opacity: 0; transform: translateX(-50px);}
    100% {opacity: 1; transform: translateX(0);}
}
.word-animate span {
    display: inline-block;
    opacity: 0;
    animation: slideInWordByWord 0.6s ease forwards;
}
.word-animate span:nth-child(1) { animation-delay: 0s; }
.word-animate span:nth-child(2) { animation-delay: 0.3s; }
.word-animate span:nth-child(3) { animation-delay: 0.6s; }
.word-animate span:nth-child(4) { animation-delay: 0.9s; }
.word-animate span:nth-child(5) { animation-delay: 1.2s; }
.word-animate span:nth-child(6) { animation-delay: 1.5s; }
.word-animate span:nth-child(7) { animation-delay: 1.8s; }

.stApp {
    background: linear-gradient(130deg, #0f0c29, #302b63, #24243e);
    background-size: 600% 600%;
    animation: gradientBG 15s ease infinite;
}
@keyframes gradientBG {
    0% {background-position: 0% 50%;}
    50% {background-position: 100% 50%;}
    100% {background-position: 0% 50%;}                                      
}
</style>
""", unsafe_allow_html=True)

# 🔥 Main Title
st.markdown("<h1 class='animated-title'>Secure, Encrypt, Protect — All in One Place.</h1>", unsafe_allow_html=True)

# 🤖 Word-by-word animation
st.markdown("""
<h2 class='word-animate' style='color: white; text-align: center;'>
    <span>Secure</span> 
    <span>Your</span> 
    <span>data</span> 
    <span>with a</span> 
    <span>futuristic</span> 
    <span>UI</span>
    <span>experience.</span>
</h2>
""", unsafe_allow_html=True)

# 🔐 Feature Cards
st.markdown("""
<h3 style='color: #E94560; text-align:center;'>✨ What You Can Do</h3>
<div style='display: flex; justify-content: center; gap: 20px; flex-wrap: wrap;'>
    <div style='background-color:#16213E; padding:20px; border-radius:12px; width: 250px;'>
        <h4 style='color:white;'>🔐 Encrypt Files</h4>
        <p style='color: grey;'>Your sensitive data locked with next-gen encryption.</p>
    </div>
    <div style='background-color:#16213E; padding:20px; border-radius:12px; width: 250px;'>
        <h4 style='color:white;'>📥 Decrypt Safely</h4>
        <p style='color: grey;'>Password-protected, limited retries.</p>
    </div>
    <div style='background-color:#16213E; padding:20px; border-radius:12px; width: 250px;'>
        <h4 style='color:white;'>🧠 Smart UI</h4>
        <p style='color: grey;'>Animations, alerts, and futuristic feels.</p>
    </div>
</div>
""", unsafe_allow_html=True)

# 🧭 How To Use
st.markdown("""
<h3 style = 'color: #E94560; text-align:center;'>✨How To Use</h3>
<div style ='display: flex; justify-content: center;gap:20px; flex-wrap:wrap;'>
            <div style ='background-color:#16213E; padding:20px;border-radius:12px;width:250;'>
              <h4 style ='color:white;>Upload & Lock It:
</h4>
              <p style='color:grey;'>Just upload your file — our own encryption logic does the magic</p>
            </div>
            <div style='background-color:#16213E; padding:20px;border-radius:12px;width:250;'>
              <h4 style='color:white;>Get Your Secret Code:
</h4>
              <p style ='color:grey;>A random, one-time password is generated for you. Guard it like a secret weapon.
</p>
            </div>
            <div style ='background-color:#16213E; padding:20px; border-radius:12px;width:250;'>
              <h4 style ='color:white;Unlock with Confidence:
></h4>
              <p style ='color:grey;>Enter the password to decrypt. No scans, no fluff — just smooth security.
</p>
           """ ,unsafe_allow_html=True)
st.markdown("""
    <style>
    div.stButton > button:first-child {
        background-color: #7209B7;
        color: white;
        font-size: 20px;
        border-radius: 12px;
        padding: 0.6em 2em;
        transition: 0.3s ease-in-out;
        box-shadow: 0 0 10px #F72585;
        text-align: center;
    }
    div.stButton > button:first-child:hover {
        background-color: #F72585;
        box-shadow: 0 0 20px #F72585;
        text-align: center;
    }{
    display: flex;
    justify-content: center;
    margin-top: 40px;}
    </style>
""", unsafe_allow_html=True)

# 🔘 Button to go to login page
if st.button("Click here to login"):
    with st.spinner("Moving to login page..."):
        time.sleep(1)
    st.switch_page("pages/3_login.py")

# 🔚 Footer
st.markdown("""
<hr style="border:1px solid #444"/>
<div style='text-align: center; color: grey; font-size: 14px;'>
    © 2025 EchoCrypt. Made with 💖 by Us.
</div>
""", unsafe_allow_html=True)
