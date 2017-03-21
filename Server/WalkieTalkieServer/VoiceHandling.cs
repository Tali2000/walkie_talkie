using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using System.IO;
using System.Diagnostics;
using System.Media;
using System.Speech.Synthesis;
using WalkieTalkieServer.Networking.Definitions;

namespace WalkieTalkieServer
{
    public static class VoiceHandling
    {
        [DllImport(@"urlmon.dll", CharSet = CharSet.Auto)]
        private extern static System.UInt32 FindMimeFromData(
            System.UInt32 pBC,
            [MarshalAs(UnmanagedType.LPStr)] System.String pwzUrl,
            [MarshalAs(UnmanagedType.LPArray)] byte[] pBuffer,
            System.UInt32 cbSize,
            [MarshalAs(UnmanagedType.LPStr)] System.String pwzMimeProposed,
            System.UInt32 dwMimeFlags,
            out System.UInt32 ppwzMimeOut,
            System.UInt32 dwReserverd
        );

        private static string getMimeFromFile(string filename)
        {
            if (!File.Exists(filename))
                throw new FileNotFoundException(filename + " not found");
            byte[] buffer = new byte[256];
            using (FileStream fs = new FileStream(filename, FileMode.Open))
            {
                if (fs.Length >= 256)
                    fs.Read(buffer, 0, 256);
                else
                    fs.Read(buffer, 0, (int)fs.Length);
            }
            try
            {
                System.UInt32 mimetype;
                FindMimeFromData(0, null, buffer, 256, null, 0, out mimetype, 0);
                System.IntPtr mimeTypePtr = new IntPtr(mimetype);
                string mime = Marshal.PtrToStringUni(mimeTypePtr);
                Marshal.FreeCoTaskMem(mimeTypePtr);
                return mime;
            }
            catch
            {
                return "unknown/unknown";
            }
        }

        public static bool IsWave(string FileName)
        {
            return "audio/wav" == getMimeFromFile(FileName);
        }

        public static string Distort(string path, DistortionType distortion)
        {
            switch(distortion)
            {
                case DistortionType.ECHO:
                    return MakeDistortion(path, "echo 0.8 0.9 1000 0.3 1800 0.25");
                case DistortionType.CHIPMUNKS:
                    return MakeDistortion(path, "speed 1.6");
                case DistortionType.LOW_VOICE:
                    return MakeDistortion(path, "speed 0.7");
                case DistortionType.METALIC:
                    return MakeDistortion(path, "stretch 1.7");
                case DistortionType.CRAZY_BABY:
                    return MakeDistortion(path, "stretch 1.5 speed 1.6");
                case DistortionType.NONE:
                default:
                    return path;
            }
        }

        private static string MakeDistortion(string path, string effect)
        {
            string tempFile = path.Replace(".wav", "output.wav");
            ProcessStartInfo startInfo = new ProcessStartInfo();
            startInfo.FileName = @"C:\Program Files (x86)\sox-14-4-2\sox.exe";
            startInfo.Arguments = $"{path} {tempFile} {effect}";
            startInfo.WindowStyle = ProcessWindowStyle.Hidden;
            startInfo.UseShellExecute = false;
            startInfo.CreateNoWindow = false;
            startInfo.WorkingDirectory = @"C:\Program Files (x86)\sox-14-4-2";
            using (Process soxProc = Process.Start(startInfo))
                soxProc.WaitForExit();
            File.Delete(path);
            return tempFile;
        }
    }
}
