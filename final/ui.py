import streamlit as st
import time
import requests
import loginpages.admin as admin_page
import loginpages.user as user_page
from utils import render_sidebar

# ------------------------- CONFIG -------------------------
API_URL = "http://localhost:8080/api/auth/login"
render_sidebar()

# ------------------------- SESSION INIT -------------------------
if "jwt_token" not in st.session_state:
    st.session_state.jwt_token = None
if "logged_in_as" not in st.session_state:
    st.session_state.logged_in_as = None

# ------------------------- STYLE -------------------------
st.markdown("""
    <style>
    .stApp {
        background-color: #1A1A2E;
    }
    h1.animated-title {
        margin-top: 0px;
        padding-top: 0px;
        animation: fadeInDown 1.5s ease-in-out;
    }
    @keyframes fadeInDown {
        0% { opacity: 0; transform: translateY(-50px); }
        100% { opacity: 1; transform: translateY(0); }
    }
    div.stButton > button:first-child {
        background-color: #7209B7;
        color: white;
        font-size: 20px;
        border-radius: 12px;
        padding: 0.6em 2em;
        transition: 0.3s ease-in-out;
        box-shadow: 0 0 10px #F72585;
        text-align: center;
        margin: 10px auto;
        display: block;
    }
    div.stButton > button:first-child:hover {
        background-color: #F72585;
        box-shadow: 0 0 20px #F72585;
    }
    </style>
""", unsafe_allow_html=True)

# ------------------------- TITLE -------------------------
st.markdown("<h1 class='animated-title' style='font-family: Verdana, sans-serif; font-size: 60px; text-align: center; color: #F72585;'>LOGIN</h1>", unsafe_allow_html=True)
st.markdown("<h4 style='text-align:center; color:white;'>Identify Yourself</h4>", unsafe_allow_html=True)

# ------------------------- AUTH FUNCTION -------------------------
def authenticate_admin_jwt(user_id, password):
    """ Send credentials to backend API as body and authenticate """
    url = API_URL
    payload = {
        "username": user_id,
        "password": password
    }

    try:
        # Send credentials as JSON body
        response = requests.post(API_URL, json=payload)
        print(response)
        # Log the response content for debugging
        st.write(f"Response: {response.text}")

        if response.status_code == 200:
            # Parse response for the JWT token
            data = response.json()
            st.session_state.jwt_token = data.get("token")
            return True
        else:
            
            return False
    except Exception as e:
        st.error(f"🚨 Failed to connect to backend: {e}")
        return False
ENCRYPTION_API_URL = "http://localhost:8080/api/user/encrypt" 
def upload_file(uploaded_file):
    """ Handle file upload """
    if uploaded_file is not None:
        st.write(f"File Name: {uploaded_file.name}")
        st.write(f"File Type: {uploaded_file.type}")
        return True
    return False
def handle_encryption(uploaded_file, email):
    try:
        if uploaded_file is None or not email:
            return False, "⚠️ Please upload a file and enter your email."

        files = {"file": uploaded_file}
        data = {"email": email}
        response = requests.post(ENCRYPTION_API_URL)

        if response.status_code == 200:
            return True, "✅ File encrypted successfully!"
        else:
            return False, f"❌ Encryption failed: {response.text}"
    except Exception as e:
        return False, f"🚨 Error: {str(e)}"
# ------------------------- UI -------------------------
login_type = st.radio("Select Login Type", ["Admin", "User"])

if login_type == "Admin":
    st.subheader("Enter Admin Credentials")
    user_id = st.text_input("User ID", placeholder="Enter Admin User ID")
    password = st.text_input("Password", type="password", placeholder="Enter Admin Password")

    if st.button("Login as Admin"):
        try:
            with st.spinner("Verifying with server..."):
                time.sleep(1)
                is_authenticated = authenticate_admin_jwt(user_id, password)
                if is_authenticated:
                    st.session_state.logged_in_as = "admin"
                    st.success("✅ Login successful! Redirecting to Admin Page...")
                    admin_page.show()
                else:
                    raise ValueError("❌ Invalid User ID or Password.")
        except ValueError as ve:
            st.error(str(ve))
        except Exception as e:
            st.error(f"An error occurred: {str(e)}")

elif login_type == "User":
    st.subheader("what you want")
    want_type = st.radio("Select Need Type", ["Encryption", "Decrpytion"])
    if want_type =="Encryption":
        uploaded_file= st.file_uploader("Upload the file", type=["txt"], key="encrypt")
        email = st.text_input("Enter your Email ID")
        if st.button("Upload & Encrypt"):
            with st.spinner("Encrypting file..."):
                time.sleep(1)
                success, msg = handle_encryption(uploaded_file, email)
                if success:
                    st.session_state.logged_in_as = "user"
                    st.success(msg)
                    user_page.show()
                else:
                    st.error(msg)

    

# ------------------------- FOOTER -------------------------
st.markdown("""
<hr style="border:1px solid #444"/>
<div style='text-align: center; color: grey; font-size: 14px;'>
    © 2025 EchoCrypt. Made with 💖 by Us.
</div>
""", unsafe_allow_html=True)