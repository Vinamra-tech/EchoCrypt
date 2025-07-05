def decrypt_file():
    try:
        data = request.get_json()
        key = data.get("key")
        password = data.get("password")
        encrypted_file_b64 = data.get("encrypted_file")

        print("Key :",key)
        print("Password :",password)
        print("encrypted_file :",encrypted_file_b64)
        encrypted_bytes=base64.b64decode(encrypted_file_b64)

        encrypted_text=encrypted_bytes.decode('utf-8')
    
        # Decode the Base64 string to get the encrypted bytes
        
        print("encrypted text",encrypted_text)
        expected_password = generate_password(encrypted_text)
        print("password",generate_password)
        if password != expected_password:
            return jsonify({"stderr": "Invalid password",
                            "stdout" :"",
                            "exitCode" :401,
                            "outputFile" :""})

        decrypted_text = substitute_decrypt(encrypted_text, key)
        # Encode the decrypted text back to bytes and then Base64 encode it
        decrypted_base64 = base64.b64encode(decrypted_text.encode('utf-8')).decode('utf-8') # Explicitly encode to UTF-8
        print("Decrypted base 64 :",decrypted_base64)
        return jsonify({
            "original_file": decrypted_base64
        })

    except Exception as e:
        return jsonify({"stderr": str(e)}), 500