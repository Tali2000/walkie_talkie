using System.Diagnostics;
using System.IO;
using System.Media;
using System.Speech.Synthesis;
using WalkieTalkieServer.Networking.Definitions;

namespace WalkieTalkieServer
{
    public static class VoiceHandling
    {
        public static bool IsPlayable(string path)
        {
            SoundPlayer sp = new SoundPlayer(path);
            try
            {
                sp.Play();
                return true;
            }
            catch
            {
                return false;
            }
        }

        public static void Distort(string path, DistortionType distortion)
        {
            switch(distortion)
            {
                case DistortionType.ECHO:
                    MakeDistortion(path, "echo 0.8 0.9 1000 0.3 1800 0.25");
                    break;
                case DistortionType.HELIUM_BALL:
                    MakeDistortion(path, "speed 1.6");
                    break;
                case DistortionType.LOW_VOICE:
                    MakeDistortion(path, "speed 0.7");
                    break;
                case DistortionType.METALIC:
                    MakeDistortion(path, "stretch 1.7");
                    break;
                case DistortionType.CRAZY_BABY:
                    MakeDistortion(path, "stretch 1.5 speed 1.6");
                    break;
                case DistortionType.NONE:
                default:
                    break;
            }
        }

        private static void MakeDistortion(string path, string effect)
        {
            string tempFile = "output" + path;
            ProcessStartInfo startInfo = new ProcessStartInfo();
            startInfo.FileName = @"C:\Program Files (x86)\sox-14-4-2\sox.exe";
            startInfo.Arguments = path + " " + tempFile + " " + effect;
            startInfo.WindowStyle = ProcessWindowStyle.Hidden;
            startInfo.UseShellExecute = false;
            startInfo.CreateNoWindow = false;
            startInfo.WorkingDirectory = @"C:\Program Files (x86)\sox-14-4-2";
            using (Process soxProc = Process.Start(startInfo))
            {
                soxProc.WaitForExit();
            }
            File.Delete(path);
            File.Copy(tempFile, path);
            File.Delete(tempFile);
        }
    }
}
