# utils.py
import streamlit as st

def render_sidebar():
    st.sidebar.page_link("pages/1_home.py", label="🏠 Home")
    st.sidebar.page_link("pages/3_Login.py", label="🔐 Login")  # <- Does this exist??
    st.sidebar.page_link("pages/4_about.py", label="📘 About")
    st.sidebar.page_link("pages/5_contact.py", label="📞 Contact")

