from diffusers import DiffusionPipeline, DPMSolverMultistepScheduler
import os

class StableDiffusionWrapper:
    def __init__(self) -> None:
        os.environ['TRANSFORMERS_CACHE'] = './cache/'
        repo_id = "CompVis/stable-diffusion-v1-4"
        pipe = DiffusionPipeline.from_pretrained(
            repo_id,
            safety_checker=None
        )

        pipe.scheduler = DPMSolverMultistepScheduler.from_config(
            pipe.scheduler.config)
        pipe.enable_sequential_cpu_offload()
        self.pipe = pipe

            
    def generate_images(self, text_prompt: str, num_images: int):
        prompt = [text_prompt] * num_images
        images = self.pipe(prompt, num_inference_steps=10).images
        return images
