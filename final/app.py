import streamlit as st
import time

# Set the page config
st.set_page_config(page_title="EchoCrypt", layout="centered")

# Background color
st.markdown("""
    <style>
    /* Background color */
    .stApp {
        background-color: #1A1A2E;
        margin-top: 0px;  /* Remove the default space from the top */
        padding-top: 0px; /* Remove the top padding */
    }

    /* Reduce the margin of the title */
    h1.animated-title {
        margin-top: 0px;  /* No space above the title */
        padding-top: 0px; /* No padding at the top */
    }

    /* Animation for the title */
    .animated-title {
        animation: fadeInDown 2s ease-in-out;
    }

    @keyframes fadeInDown {
        0% { opacity: 0; transform: translateY(-50px); }
        100% { opacity: 1; transform: translateY(0); }
    }

    /* Reduce spacing for other elements as needed */
    h2 {
        margin-top: 0px;
    }

    </style>
""", unsafe_allow_html=True)

# Hide sidebar + center main area
st.markdown("""
    <style>
    [data-testid="stSidebar"] { display: none; }
    .css-18e3th9 {
        padding-left: 5rem;
        padding-right: 5rem;
    }
    </style>
""", unsafe_allow_html=True)

# Custom button styling
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

# Main title
st.markdown("<h1 class='animated-title' style='font-family: Verdana, sans-serif; font-size: 60px; text-align: center; color: #F72585;'>Welcome to EchoCrypt</h1>", unsafe_allow_html=True)

# Tagline
st.markdown("<h2 class='animated-title' style='font-size: 30px; color: #ddd;'>EchoCrypt isn’t just another tool — it’s your personal fortress in the digital world, built to guard your messages, protect your data, and keep your privacy locked tight, no matter where you are.</h2>", unsafe_allow_html=True)

# Feature List
st.markdown("""
- 🔐 **Encrypt & Decrypt** your messages securely  
- 📦 Store your files safely & access with ease  
- 🎯 One-click file deletion for sensitive content  
- 💡 Minimal UI, maximum security – built for speed & trust  
- 🧠 Smart encryption workflows – **you focus, we protect**  
""")

# Subheading
# Animated "Let's Start" Heading with Glow & Gradient
st.markdown("""
<h2 class='animated-title' style='
    font-family: Poppins, sans-serif; 
    font-size: 55px; 
    text-align: center; 
    background: -webkit-linear-gradient(#F72585, #7209B7); 
    -webkit-background-clip: text; 
    -webkit-text-fill-color: transparent;
    text-shadow: 0 0 15px rgba(247, 37, 133, 0.5);
'>
    Let's Start
</h2>
""", unsafe_allow_html=True)


# Word-by-word animation
st.markdown("""
<style>
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
</style>
""", unsafe_allow_html=True)

st.markdown("""
<h2 class='word-animate' style='color: white; text-align: center;'>
    <span>Secure</span> 
    <span>Your</span> 
    <span>World</span> 
    <span>With</span> 
    <span>Echo</span> 
    <span>Crypt</span>
</h2>
""", unsafe_allow_html=True)

# ✅ REAL Streamlit button for navigation
st.markdown("<div style='text-align:center; margin-top: 40px;'>", unsafe_allow_html=True)


if st.button("Click Here"):
    st.switch_page("pages/1_Home.py")  # 🔁 Navigates to Home page
st.markdown("</div>", unsafe_allow_html=True)

# Footer
st.markdown("""
<hr style="border:1px solid #444"/>
<div style='text-align: center; color: grey; font-size: 14px;'>
    © 2025 EchoCrypt. Made with 💖 by Us .
</div>
""", unsafe_allow_html=True)
