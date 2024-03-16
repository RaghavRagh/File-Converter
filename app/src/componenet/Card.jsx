import { useRef, useState, useEffect } from "react";
import { FaRegFileAlt } from "react-icons/fa";
import { LuUpload } from "react-icons/lu";

import axios from "axios";

const Card = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [requestInProgress, setRequestInProgress] = useState(false);
  const [convertedFile, setConvertedFile] = useState(null);
  const fileInputRef = useRef(null);

  useEffect(() => {
    if (selectedFile !== null) {
      handleUpload();
    }
  }, [selectedFile]);

  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    const fileSize = file.size;
    setSelectedFile(file);
    console.log("Selected file: ", file.name, fileSize);
  };

  const handleUpload = async () => {
    console.log("request sending");
    setRequestInProgress(true);

    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
      const response = await axios.post(
        "http://localhost:8080/api/convert/convert-to-pdf",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          responseType: "blob",
        }
      );

      if (response.status === 200) {
        console.log("File converted successfully:", response.data);
        setConvertedFile(response.data);
      } else if (response.status === 500) {
        console.log("Only word file accepted");
      } else {
        console.error("Error converting file:", response.statusText);
      }
    } catch (error) {
      console.error("Error sending request:", error);
    } finally {
      setRequestInProgress(false);
    }
  };

  const handleClick = (e) => {
    e.stopPropagation();
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const handleDownload = () => {
    if (convertedFile) {
      const url = window.URL.createObjectURL(new Blob([convertedFile]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'converted.pdf');
      document.body.appendChild(link);
      link.click();
    }
  }

  return (
    <div className="absolute left-1/2 top-[60%] -translate-x-[50%] -translate-y-[50%] w-[70vw] h-[70vh] rounded-[40px] bg-zinc-900/90 text-white px-8 py-10 overflow-hidden shadow-xl">
      <p className="text-xl mt-5 text-zinc-600">Upload files</p>

      {selectedFile && (
        <div className="bg-zinc-800/60 rounded-md px-8 py-3 my-3 shadow-lg flex justify-between items-center">
          <p className="text-lg text-zinc-200 flex items-center gap-4">
            <FaRegFileAlt />{" "}
            {`File: ${selectedFile.name} | size: ${selectedFile.size}`}
          </p>

          {requestInProgress && <p className="text-zinc-400">Converting...</p>}

          {!requestInProgress && (
            <div className="flex items-center gap-4">
              <button
                className="bg-green-600 font-semibold px-5 py-2 rounded-sm shadow-md hover:bg-green-500 transition ease-in-out delay-80"
                onClick={handleDownload}
                disabled={!convertedFile}
              >
                Download
              </button>
            </div>
          )}
        </div>
      )}

      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileUpload}
        style={{ display: "none" }}
      />

      <div className="footer absolute bottom-0 w-full left-0">
        <div className="flex items-center justify-between px-8 py-3 mb-3"></div>
        <button
          className="tag w-full py-4 bg-green-500/90 flex items-center justify-center hover:bg-green-600"
          onClick={handleClick}
          disabled={requestInProgress}
        >
          <div className="flex items-center justify-between gap-1">
            <h3 className="text-lg font-semibold">Upload files</h3>
            <span className="w-8 h-8 flex items-center justify-center">
              <LuUpload size="1.2em" />
            </span>
          </div>
        </button>
      </div>
    </div>
  );
};

export default Card;
