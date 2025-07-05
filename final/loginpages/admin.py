import streamlit as st
import requests
import pandas as pd
from datetime import datetime

def get_auth_headers():
    token = st.session_state.get("jwt_token")
    if token:
        return {"Authorization": f"Bearer {token}"}
    return {}

def show():
    st.title("📁 File Logs Dashboard")

    if "input_stage" not in st.session_state:
        st.session_state.input_stage = None

    if st.button("Reset View 🔄"):
        st.session_state.input_stage = None

    # ------------ ACTION BUTTONS ------------
    if st.button("🟢 Get All Logs"):
     try:
        res = requests.get("http://localhost:8080/api/admin/logs/all", headers=get_auth_headers())
        print(get_auth_headers())
        if res.status_code == 200:
            data = res.json()
            if data:
                st.subheader("📜 Logs List")
                for log in data:
                    with st.expander(f"🗂️ Log ID: {log['id']}"):
                        st.markdown(f"""
                        - **Filename**: {log.get('originalFileName', 'N/A')}
                        - **encrpted files**: {log.get('encryptedFileName', 'N/A')}
                        - **decrpted file**: {log.get('decryptedFileName', 'N/A')}
                        - **Operation type**: {log.get('operationType', 'N/A')}
                        - **Operation time**: {log.get('operationTime', 'N/A')}
                        - **status**:{log.get('status','N/A')}
                        - **remarks**:{log.get('remarks','N/A')}
                        """)
            else:
                st.info("📭 No logs available.")
        else:
            st.error(f"❌ Failed to fetch all logs. Status code: {res.status_code}")
     except Exception as e:
        st.error(f"🚨 Error occurred while fetching logs: {e}")



    if st.button("🔍 Get Logs by Status"):
        st.session_state.input_stage = "status"

    if st.button("🔐 Get Logs by Type"):
        st.session_state.input_stage = "type"

    if st.button("🔢 Get Log by ID"):
        st.session_state.input_stage = "id"

    if st.button("🗓️ Get Logs by Range"):
        st.session_state.input_stage = "range"

    # ------------ INPUT STAGES ------------
    if st.session_state.input_stage == "status":
     status = st.text_input("Enter Status (Success / Failure):")
    if st.button("submit"):
        res = requests.get(
            "http://localhost:8080/api/admin/logs/status",
            params={"status": status},
            headers=get_auth_headers()
        )
        if res.status_code == 200:
            data = res.json()
            df = pd.DataFrame(data)
            df.index = range(1, len(df) + 1)  
            st.dataframe(df)
        else:
            st.error("❌ Failed to fetch logs by status")


    elif st.session_state.input_stage == "type":
        log_type = st.text_input("Enter Log Type ( ENCRYPTION/ DECRYPTION):")
        if st.button("Submit Type"):
            res = requests.get(
                "http://localhost:8080/api/admin/logs/type",
                params={"type": log_type},
                headers=get_auth_headers()
            )
            if res.status_code == 200:
                data = res.json()
                df = pd.DataFrame(data)
                df.index = range(1, len(df) + 1)  
                st.dataframe(df)
            else:
                st.error("❌ Failed to fetch logs by type")

    elif st.session_state.input_stage == "id":
        log_id = st.text_input("🔎 Enter Log ID:")
        
        if "submitted_id" not in st.session_state:
            st.session_state.submitted_id = False

        if st.button("Submit ID") and not st.session_state.submitted_id:
            st.session_state.submitted_id = True  

            try:
                log_id_int = int(log_id)
                res = requests.get(
                    "http://localhost:8080/api/admin/log/id",
                    params={"id": log_id_int},
                    headers=get_auth_headers()
                )

                if res.status_code == 200:
                    data = res.json()

                    # Normalize response
                    if isinstance(data, dict):
                        data = [data]

                    if data and isinstance(data, list) and len(data) > 0:
                        st.success("✅ Log found!")
                        st.dataframe(pd.DataFrame(data))
                    else:
                        st.info("📭 Log ID exists but returned no data.")
                else:
                    st.warning(f"⚠️ No log found with ID {log_id_int} (Status: {res.status_code})")

            except ValueError:
                st.warning("⚠️ Please enter a valid numeric ID.")

    elif st.session_state.input_stage == "range":
        st.markdown("### Select Date & Time Range")
        start_date = st.date_input("Start Date:")
        start_time = st.time_input("Start Time:")
        end_date = st.date_input("End Date:")
        end_time = st.time_input("End Time:")

        if st.button("Submit Range"):
            try:
                start_dt = datetime.combine(start_date, start_time).isoformat()
                end_dt = datetime.combine(end_date, end_time).isoformat()
                res = requests.get(
                    "http://localhost:8080/api/admin/logs/range",
                    params={"start": start_dt, "end": end_dt},
                    headers=get_auth_headers()
                )
                if res.status_code == 200:
                    data = res.json()
                    df = pd.DataFrame(data)
                    df.index = range(1, len(df) + 1)  # 👈 set index starting from 1
                    st.dataframe(df)
                else:
                    st.error("❌ Failed to fetch logs by range")
            except Exception as e:
                st.warning(f"⚠️ Error: {e}")
