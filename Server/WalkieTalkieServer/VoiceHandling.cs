using System.IO;
using System.Media;
using System.Speech.Synthesis;

namespace WalkieTalkieServer
{
    public static class VoiceHandling
    {
        /*public static void Check()
        {
            SpeechSynthesizer synt = new SpeechSynthesizer();
            synt.SelectVoiceByHints(VoiceGender.Male,VoiceAge.Child);
            synt.Speak("hello world");

            //SoundPlayer sp = new SoundPlayer();
            //sp.SoundLocation = @"C:\Users\User\Desktop\Herman's Hermits - No Milk Today.wav";
            //sp.PlaySync();
        }

        //It doesn't work!!!
        public static void PlusByte()
        {
            byte[] arr = File.ReadAllBytes(@"C:\Users\User\Desktop\Herman's Hermits - No Milk Today.wav");
            string newPath = @"C:\Users\User\Desktop\temp.wav";
            for (int i = 0; i < arr.Length; i++)
                arr[i] += sizeof(byte);
            File.WriteAllBytes(newPath, arr);

            SoundPlayer sp = new SoundPlayer();
            sp.SoundLocation = newPath;
            sp.PlaySync();
        }*/

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
    }
}
