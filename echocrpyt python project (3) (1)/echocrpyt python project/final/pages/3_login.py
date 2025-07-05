import streamlit as st
import time
import requests
import loginpages.admin as admin_page
import loginpages.user as user_page
from utils import render_sidebar
import os
# ------------------------- CONFIG -------------------------
API_URL = "http://localhost:8080/api/auth/login"
ENCRYPTION_API_URL = "http://localhost:8080/api/user/encrypt"
DECRYPTION_API_URL = "http://localhost:8080/api/user/decrypt"
render_sidebar()

# ------------------------- SESSION INIT -------------------
if "jwt_token" not in st.session_state:
    st.session_state.jwt_token = None
if "logged_in_as" not in st.session_state:
    st.session_state.logged_in_as = None
if "uploaded_file" not in st.session_state:
    st.session_state.uploaded_file = None

# ------------------------- STYLE -------------------------
st.markdown("""
    <style>
    .stApp { background-color: #1A1A2E; }
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
    payload = {"userName": user_id, "password": password}
    try:
        response = requests.post(API_URL, json=payload)
        if response.status_code == 200:
            data = response.json()
            st.session_state.jwt_token = data.get("token")
            
            return True
        return False
    except Exception as e:
        st.error(f"🚨 Backend error: {e}")
        return False

# ------------------------- ENCRYPTION -------------------------
def handle_encryption(uploaded_file, email):
    if uploaded_file is None or not email:
        return False, "⚠️ Please upload a file and enter your email."

    progress_bar = st.progress(0)
    progress_bar.progress(50)

    files = {"file": (uploaded_file.name, uploaded_file, uploaded_file.type)}
    data = {"email": email}
    time.sleep(1)

    path = r"C:\Users\sabha\OneDrive\Documents\web development\html\tables\resume"
    filename = uploaded_file.name 
    full_path = os.path.join(path, filename)

    try:
        response = requests.post(ENCRYPTION_API_URL, files=files, data=data)
    except Exception as e:
        return False, f"❌ Request failed: {e}"

    progress_bar.progress(100)
    progress_bar.empty()

    if response.status_code == 200:
        st.session_state.uploaded_file = uploaded_file
        try:
            result = response.json()
            password = result.get("password")
            file6 = result.get("zipFileName")

            if password:
                st.success("✅ File encrypted successfully!")
                st.success(f"📁 Encrypted File Name: {file6}")

                # 💣 delete original file
                try:
                    os.remove(full_path)
                except Exception as e:
                    st.warning(f"⚠️ File not deleted: {e}")

                return True, f"🔑 Your password is: {password}"
            else:
                return True, "✅ File encrypted successfully! (No password returned.)"
        except Exception as e:
            return False, f"❌ Failed to parse server response: {e}"

    return False, f"❌ Encryption failed: {response.text}"




# ------------------------- DECRYPTION -------------------------



def handle_decryption(uploaded_filezip, password):
    if uploaded_filezip is None or not password:
        return False, "⚠️ Please upload a file and enter your password."

    progress_bar = st.progress(0)
    progress_bar.progress(50)

    files = {"file": (uploaded_filezip.name, uploaded_filezip, uploaded_filezip.type)}
    data = {"password": password}
    time.sleep(1)
    response = requests.post(DECRYPTION_API_URL, files=files, data=data)
    progress_bar.progress(100)
    progress_bar.empty()

    if response.status_code == 200:
        st.session_state.uploaded_filezip = uploaded_filezip
        try:
            result = response.json()  #  response JSON
            print(f"Decryption result: {result}")  
            decrypted_filename = result.get('fileName', None)
            if decrypted_filename:
                st.success(f"✅ Decryption Successful! Filename: {decrypted_filename}")
            else:
                st.error("❌ No filename returned by server.")
                return False, "❌ Decryption failed: No filename returned."

        
            upload_dir = r"C:\Users\sabha\OneDrive\Desktop\encrypt-decrypt-backend\encrypted_files"
            uploaded_file_path = os.path.join(upload_dir, uploaded_filezip.name)

            print(f"Deleting file at: {uploaded_file_path}")

            # Delete the encrypted file after decryption
            try:
                if os.path.exists(uploaded_file_path):
                    os.remove(uploaded_file_path)
                    st.success("✅ Encrypted file deleted.")
                else:
                    st.warning(f"⚠️ File '{uploaded_filezip.name}' not found at path.")
            except Exception as e:
                st.error(f"❌ Failed to delete encrypted file: {e}")

            return True, f"✅ Decryption Successful! Filename: {decrypted_filename}"
        except Exception as e:
            st.error(f"❌ Failed to parse server response: {e}")
            return False, f"❌ Decryption failed: {e}"

    return False, f"❌ Decryption failed: {response.text}"



# ------------------------- UI -------------------------
login_type = st.radio("Select Login Type", ["Admin", "User"])

if login_type == "Admin":
    if st.session_state.jwt_token:
        st.success("You are already logged in as Admin.")
        st.session_state.logged_in_as = "admin"
        admin_page.show()
    else:
        st.subheader("Enter Admin Credentials")
        user_id = st.text_input("User ID", placeholder="Enter Admin User ID")
        password = st.text_input("Password", type="password", placeholder="Enter Admin Password")
        if st.button("Login as Admin"):
            with st.spinner("Verifying with server..."):
                if authenticate_admin_jwt(user_id, password):
                    st.session_state.logged_in_as = "admin"
                    st.success("✅ Login successful! Redirecting to Admin Page...")
                    admin_page.show()
                else:
                    st.error("❌ Invalid credentials.")

elif login_type == "User":
    st.subheader("What do you want to do?")
    want_type = st.radio("Select Action", ["Encryption", "Decryption"])

    if want_type == "Encryption":
        uploaded_file = st.file_uploader("Upload a `.txt` file", type=["txt"], key="encrypt")
        email = st.text_input("Enter your Email ID")

        if st.button("Upload & Encrypt"):
            with st.spinner("Encrypting..."):
                success, msg = handle_encryption(uploaded_file, email)
                if success:
                    st.session_state.logged_in_as = "user"
                    st.success(msg)
                    user_page.show()
                else:
                    st.error(msg)

        if st.session_state.uploaded_file:
            st.subheader("Uploaded File Preview")
            try:
                file_content = st.session_state.uploaded_file.getvalue().decode("utf-8")
                st.text_area("File Content", value=file_content, height=300)
            except Exception as e:
                st.error(f"Error reading file: {e}")

    elif want_type == "Decryption":
        uploaded_filezip = st.file_uploader("Upload a `.zip` file", type=["zip"], key="decrypt")
        password = st.text_input("Enter your Password")

        if st.button("Upload & Decrypt"):
            with st.spinner("Decrypting..."):
                success, msg = handle_decryption(uploaded_filezip, password)
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
